package com.example.myapplication.player

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.myapplication.data.MetricsRepository
import com.example.myapplication.model.VideoItem
import com.example.myapplication.model.defaultQuality
import com.example.myapplication.model.defaultPlaybackUrl
import com.example.myapplication.model.findQuality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@UnstableApi
class PlayerManager(
    context: Context,
    private val metricsRepository: MetricsRepository? = null,
    private val onVideoUrlExpired: (suspend (String) -> VideoItem?)? = null,
) {

    private val appContext = context.applicationContext

    var player by mutableStateOf(createPlayer())
        private set

    var state by mutableStateOf(VideoPlayerState())
        private set

    private var latestMetrics by mutableStateOf<PlaybackMetrics?>(null)

    val currentVideoUrl: String?
        get() = state.videoUrl

    private val metricsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var currentVideoId: String? = null
    private var prepareStartTimeMs: Long = 0L
    private var currentWasPreloaded: Boolean = false
    private var currentColdBaselineMs: Long = 0L

    var preloadEnabled: Boolean = true

    private var retryCount: Int = 0
    private var maxRetries: Int = MAX_RETRIES
    private var lastVideoItem: VideoItem? = null

    private var preloadPlayer: ExoPlayer? = null
    private var preloadListener: Player.Listener? = null
    private var preloadVideo: VideoItem? = null
    private var preloadStartTimeMs: Long = 0L
    private var preloadReadyTimeMs: Long = 0L

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            state = state.copy(
                isLoading = playbackState == Player.STATE_BUFFERING,
                durationMs = player.safeDuration(),
                positionMs = player.currentPosition.coerceAtLeast(0L),
            )

            if (playbackState == Player.STATE_READY) {
                recordFirstReady()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            state = state.copy(
                isPlaying = isPlaying,
                isLoading = player.playbackState == Player.STATE_BUFFERING,
            )
        }

        override fun onPlayerError(error: PlaybackException) {
            retryCount++
            val canRetry = retryCount < maxRetries
            state = state.copy(
                isPlaying = false,
                isLoading = false,
                hasError = true,
                errorMessage = error.localizedMessage,
                canRetry = canRetry,
                retryCount = retryCount,
            )
            if (canRetry) {
                scheduleRetry()
            } else {
                // 可能是 URL 过期 → 尝试刷新
                val videoId = currentVideoId
                val handler = onVideoUrlExpired
                if (videoId != null && handler != null) {
                    metricsScope.launch {
                        try {
                            Log.d(TAG, "URL may be expired, refreshing: $videoId")
                            val refreshed = handler(videoId)
                            if (refreshed != null) {
                                Log.d(TAG, "URL refreshed, retrying playback")
                                retryCount = 0
                                play(refreshed)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Refresh/retry failed: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    init {
        player.addListener(listener)
    }

    fun play(videoItem: VideoItem) {
        val targetQuality = videoItem.defaultQuality()
        val targetUrl = videoItem.defaultPlaybackUrl()

        if (state.videoUrl == targetUrl && currentVideoId == videoItem.id) {
            player.playWhenReady = true
            player.play()
            refreshProgress()
            return
        }

        lastVideoItem = videoItem
        retryCount = 0

        val hasPreparedPreload = preloadVideo?.id == videoItem.id &&
            preloadVideo?.defaultPlaybackUrl() == targetUrl &&
            preloadPlayer != null &&
            preloadReadyTimeMs > 0L
        if (preloadVideo?.id == videoItem.id && !hasPreparedPreload) {
            clearPreload()
        }
        val matchedPreload = hasPreparedPreload
        val now = System.currentTimeMillis()

        currentVideoId = videoItem.id
        currentWasPreloaded = matchedPreload
        currentColdBaselineMs = if (matchedPreload && preloadReadyTimeMs > preloadStartTimeMs) {
            preloadReadyTimeMs - preloadStartTimeMs
        } else {
            0L
        }
        prepareStartTimeMs = now
        state = VideoPlayerState(
            videoId = videoItem.id,
            videoUrl = targetUrl,
            qualityLabel = targetQuality.label,
            isLoading = true,
        )

        try {
            if (matchedPreload) {
                promotePreloadPlayer()
            } else {
                player.setMediaItem(MediaItem.fromUri(targetUrl))
                player.prepare()
            }
            player.playWhenReady = true
            player.play()
        } catch (e: Exception) {
            Log.e(TAG, "Play failed: ${e.message}")
            state = state.copy(isLoading = false, hasError = true, errorMessage = "播放失败")
        }

        if (player.playbackState == Player.STATE_READY) {
            recordFirstReady()
        }
        refreshProgress()
    }

    fun play(videoUrl: String) {
        play(
            VideoItem(
                id = videoUrl,
                title = "",
                description = "",
                authorName = "",
                authorAvatar = "",
                videoUrl = videoUrl,
                coverUrl = "",
                durationText = "",
                likeCount = "",
                commentCount = "",
                collectCount = "",
                shareCount = "",
                tags = emptyList(),
                recommendWords = emptyList(),
            )
        )
    }

    fun preloadNext(videoItem: VideoItem) {
        if (!preloadEnabled) return
        preloadInternal(videoItem)
    }

    /** 预加载上一个视频 (向上滑回去时使用) */
    fun preloadPrevious(videoItem: VideoItem) {
        if (!preloadEnabled) return
        // 只在没有进行中的 preload 时才预加载上一个 (优先保下一个)
        if (preloadPlayer != null) return
        preloadInternal(videoItem)
    }

    private fun preloadInternal(videoItem: VideoItem) {
        val targetUrl = videoItem.defaultPlaybackUrl()
        if (targetUrl == state.videoUrl || preloadVideo?.id == videoItem.id) return

        clearPreload()

        val nextPlayer = createPlayer()
        val startTimeMs = System.currentTimeMillis()
        val nextListener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && preloadVideo?.id == videoItem.id) {
                    preloadReadyTimeMs = System.currentTimeMillis()
                }
            }
            override fun onPlayerError(error: PlaybackException) { clearPreload() }
        }

        preloadPlayer = nextPlayer
        preloadListener = nextListener
        preloadVideo = videoItem
        preloadStartTimeMs = startTimeMs
        preloadReadyTimeMs = 0L

        nextPlayer.addListener(nextListener)
        nextPlayer.setMediaItem(MediaItem.fromUri(targetUrl))
        nextPlayer.prepare()
    }

    fun switchQuality(videoItem: VideoItem, targetQualityLabel: String) {
        val targetQuality = videoItem.findQuality(targetQualityLabel) ?: return
        if (state.videoId != videoItem.id || state.videoUrl == targetQuality.url) return

        val currentPosition = player.currentPosition.coerceAtLeast(0L)
        val shouldResume = player.isPlaying

        clearPreload()
        currentVideoId = videoItem.id
        currentWasPreloaded = false
        currentColdBaselineMs = 0L
        prepareStartTimeMs = System.currentTimeMillis()
        state = state.copy(
            videoId = videoItem.id,
            videoUrl = targetQuality.url,
            qualityLabel = targetQuality.label,
            isLoading = true,
            hasError = false,
            errorMessage = null,
        )

        player.setMediaItem(MediaItem.fromUri(targetQuality.url))
        player.prepare()
        player.seekTo(currentPosition)
        player.playWhenReady = shouldResume
        if (shouldResume) {
            player.play()
        } else {
            player.pause()
        }
        refreshProgress()
    }

    fun clearPreload() {
        preloadListener?.let { listener ->
            preloadPlayer?.removeListener(listener)
        }
        preloadPlayer?.release()
        preloadPlayer = null
        preloadListener = null
        preloadVideo = null
        preloadStartTimeMs = 0L
        preloadReadyTimeMs = 0L
    }

    fun getPlaybackMetrics(): PlaybackMetrics? = latestMetrics

    fun pause() {
        player.pause()
        refreshProgress()
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            pause()
        } else {
            player.play()
            refreshProgress()
        }
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs.coerceAtLeast(0L))
        refreshProgress()
    }

    fun refreshProgress() {
        state = state.copy(
            isPlaying = player.isPlaying,
            isLoading = player.playbackState == Player.STATE_BUFFERING,
            positionMs = player.currentPosition.coerceAtLeast(0L),
            durationMs = player.safeDuration(),
        )
    }

    fun retry() {
        val videoItem = lastVideoItem ?: return
        retryCount = 0
        state = state.copy(hasError = false, errorMessage = null, canRetry = false, retryCount = 0)
        play(videoItem)
    }

    private fun scheduleRetry() {
        val delayMs = RETRY_DELAY_BASE_MS * retryCount
        metricsScope.launch {
            delay(delayMs)
            val videoItem = lastVideoItem ?: return@launch
            Log.d(TAG, "Auto-retry #$retryCount for video: ${videoItem.id} after ${delayMs}ms")
            play(videoItem)
        }
    }

    fun release() {
        clearPreload()
        player.removeListener(listener)
        player.release()
        state = VideoPlayerState()
        latestMetrics = null
    }

    private fun promotePreloadPlayer() {
        val nextPlayer = preloadPlayer ?: return
        preloadListener?.let { nextPlayer.removeListener(it) }

        player.removeListener(listener)
        player.pause()
        player.release()

        player = nextPlayer
        player.addListener(listener)

        preloadPlayer = null
        preloadListener = null
        preloadVideo = null
        preloadStartTimeMs = 0L
        preloadReadyTimeMs = 0L
    }

    private fun recordFirstReady() {
        val startTimeMs = prepareStartTimeMs
        val videoId = currentVideoId ?: return
        if (startTimeMs <= 0L) return

        val readyTimeMs = System.currentTimeMillis()
        val readyCostMs = (readyTimeMs - startTimeMs).coerceAtLeast(0L)
        val metrics = PlaybackMetrics(
            videoId = videoId,
            videoUrl = state.videoUrl.orEmpty(),
            prepareStartTimeMs = startTimeMs,
            firstReadyTimeMs = readyTimeMs,
            firstFrameTimeMs = readyTimeMs,
            coldStartPrepareMs = if (currentWasPreloaded) currentColdBaselineMs else readyCostMs,
            preloadPrepareMs = if (currentWasPreloaded) readyCostMs else 0L,
            isPreloaded = currentWasPreloaded,
        )

        latestMetrics = metrics
        prepareStartTimeMs = 0L
        logMetrics(metrics)
    }

    private fun logMetrics(metrics: PlaybackMetrics) {
        Log.d(
            TAG,
            """
            VideoStartMetrics:
            videoId=${metrics.videoId}
            isPreloaded=${metrics.isPreloaded}
            coldStartPrepareMs=${metrics.coldStartPrepareMs}
            preloadPrepareMs=${metrics.preloadPrepareMs}
            improvement=${metrics.improvementPercent}%
            """.trimIndent()
        )

        // Machine-parseable single-line CSV for automated measurement
        Log.d(
            "MetricsCSV",
            "${metrics.videoId},cold=${metrics.coldStartPrepareMs},preload=${metrics.preloadPrepareMs},hit=${metrics.isPreloaded},improve=${metrics.improvementPercent}%"
        )

        metricsScope.launch {
            metricsRepository?.recordMetrics(metrics, state.qualityLabel.orEmpty())
        }
    }

    private fun createPlayer(): ExoPlayer {
        return ExoPlayer.Builder(appContext).build()
    }

    private fun ExoPlayer.safeDuration(): Long {
        return duration.takeIf { it != C.TIME_UNSET && it > 0L } ?: 0L
    }

    private companion object {
        const val TAG = "PlayerManager"
        const val MAX_RETRIES = 3
        const val RETRY_DELAY_BASE_MS = 500L
    }
}
