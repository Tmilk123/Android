package com.example.myapplication.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.myapplication.model.VideoItem
import com.example.myapplication.player.FullscreenController
import com.example.myapplication.player.PlayerManager
import com.example.myapplication.player.findActivity
import com.example.myapplication.player.formatPlaybackTime
import kotlinx.coroutines.delay

@UnstableApi
@Composable
fun VideoFeedCard(
    item: VideoItem,
    isActive: Boolean,
    playerManager: PlayerManager,
    isLandscapeFullscreen: Boolean = false,
    onLandscapeToggle: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val playerState = playerManager.state
    val isCurrentVideo = playerState.videoId == item.id
    val context = LocalContext.current
    val fullscreenController = remember(context) {
        context.findActivity()?.let { FullscreenController(it) }
    }
    var isQualityMenuOpen by remember(item.id) { mutableStateOf(false) }
    // Use external state if provided, otherwise internal
    var internalLandscape by remember(item.id) { mutableStateOf(false) }
    val inLandscape = if (onLandscapeToggle != null) isLandscapeFullscreen else internalLandscape
    val toggleLandscape: (Boolean) -> Unit = onLandscapeToggle ?: { internalLandscape = it }

    // Show play/pause indicator briefly after state change
    var showPlayIndicator by remember { mutableStateOf(false) }
    var lastPlayingState by remember { mutableStateOf(false) }

    // Auto-hide controls in landscape
    var showLandscapeControls by remember { mutableStateOf(false) }

    LaunchedEffect(isActive, item.videoUrl) {
        if (isActive) {
            playerManager.play(item)
        } else if (isCurrentVideo) {
            playerManager.pause()
        }
    }

    // Progress refresh loop
    LaunchedEffect(isActive, item.videoUrl) {
        while (isActive) {
            playerManager.refreshProgress()
            delay(250)
        }
    }

    // Play indicator auto-dismiss
    LaunchedEffect(playerState.isPlaying) {
        if (isCurrentVideo && playerState.isPlaying != lastPlayingState) {
            lastPlayingState = playerState.isPlaying
            showPlayIndicator = true
            delay(800)
            showPlayIndicator = false
        }
    }

    DisposableEffect(fullscreenController) {
        onDispose { fullscreenController?.exitLandscapeFullscreen() }
    }

    val videoReady = isCurrentVideo && !playerState.isLoading && !playerState.hasError

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = isActive
            ) { playerManager.togglePlayPause() },
    ) {
        // ── Layer 1: Black background (no cover image) ──

        // ── Layer 2: Video surface ──
        if (isActive) {
            AnimatedVisibility(
                visible = videoReady,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(),
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = false
                            // 横屏时 ZOOM 填满屏幕, 竖屏时 FIT 留黑边
                            resizeMode = if (inLandscape)
                                AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            else
                                AspectRatioFrameLayout.RESIZE_MODE_FIT
                            player = playerManager.player
                        }
                    },
                    update = { view -> view.player = playerManager.player },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // ── Layer 3: Gradient overlay (hidden in landscape) ──
        if (!inLandscape) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.20f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.74f),
                            )
                        )
                    )
            )
        }

        // ── Layer 4: Loading / Error / Play indicator ──
        when {
            isCurrentVideo && playerState.isLoading -> {
                ShimmerLoadingOverlay(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                )
            }

            isCurrentVideo && playerState.hasError -> {
                ErrorOverlay(
                    canRetry = playerState.canRetry,
                    retryCount = playerState.retryCount,
                    onRetry = { playerManager.retry() },
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            isCurrentVideo && showPlayIndicator -> {
                AnimatedVisibility(
                    visible = showPlayIndicator,
                    enter = fadeIn(tween(150)) + scaleIn,
                    exit = fadeOut(tween(400)),
                ) {
                    PlayPauseIcon(
                        isPlaying = playerState.isPlaying,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }

        // ── Layer 5: Duration badge (on inactive pages, hidden in landscape) ──
        if (!isActive && !inLandscape && item.durationText.isNotBlank()) {
            Text(
                text = item.durationText,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 96.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }

        // ── Layer 6: Controls (only on current video) ──
        if (isCurrentVideo) {
            if (inLandscape) {
                // ═══ LANDSCAPE: minimal immersive UI ═══
                // Tap anywhere to toggle controls visibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { showLandscapeControls = !showLandscapeControls },
                )

                // Auto-hide controls after 3s
                LaunchedEffect(showLandscapeControls) {
                    if (showLandscapeControls) {
                        delay(3000)
                        showLandscapeControls = false
                    }
                }

                AnimatedVisibility(
                    visible = showLandscapeControls,
                    enter = fadeIn(tween(200)),
                    exit = fadeOut(tween(200)),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Exit fullscreen (top left)
                        Text(
                            text = "← 返回",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 16.dp, start = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .clickable {
                                    showLandscapeControls = false
                                    toggleLandscape(false)
                                    fullscreenController?.exitLandscapeFullscreen()
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                        )

                        // Quality selector (top right, if available)
                        if (item.qualityUrls.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 16.dp, end = 12.dp),
                            ) {
                                Text(
                                    text = playerState.qualityLabel ?: item.qualityUrls.first().label,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.Black.copy(alpha = 0.55f))
                                        .clickable { isQualityMenuOpen = true }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                                DropdownMenu(
                                    expanded = isQualityMenuOpen,
                                    onDismissRequest = { isQualityMenuOpen = false },
                                ) {
                                    item.qualityUrls.forEach { quality ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (quality.label == playerState.qualityLabel)
                                                        "● ${quality.label}" else "  ${quality.label}"
                                                )
                                            },
                                            onClick = {
                                                isQualityMenuOpen = false
                                                playerManager.switchQuality(item, quality.label)
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        // Minimal progress bar (bottom)
                        PlaybackProgress(
                            positionMs = playerState.positionMs,
                            durationMs = playerState.durationMs,
                            onSeek = playerManager::seekTo,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                        )
                    }
                }
            } else {
                // ═══ PORTRAIT: standard controls ═══
                // Landscape toggle
                Text(
                    text = "⛶ 横屏",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 150.dp, end = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.48f))
                        .clickable {
                            toggleLandscape(true)
                            fullscreenController?.enterLandscapeFullscreen()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )

                // Quality selector
                if (item.qualityUrls.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 108.dp, end = 12.dp),
                    ) {
                        Text(
                            text = playerState.qualityLabel ?: item.qualityUrls.first().label,
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.48f))
                                .clickable { isQualityMenuOpen = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                        DropdownMenu(
                            expanded = isQualityMenuOpen,
                            onDismissRequest = { isQualityMenuOpen = false },
                        ) {
                            item.qualityUrls.forEach { quality ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (quality.label == playerState.qualityLabel)
                                                "● ${quality.label}" else "  ${quality.label}"
                                        )
                                    },
                                    onClick = {
                                        isQualityMenuOpen = false
                                        playerManager.switchQuality(item, quality.label)
                                    },
                                )
                            }
                        }
                    }
                }

                // Progress bar
                PlaybackProgress(
                    positionMs = playerState.positionMs,
                    durationMs = playerState.durationMs,
                    onSeek = playerManager::seekTo,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(start = 12.dp, end = 12.dp, bottom = 4.dp),
                )
            }
        }
    }
}

// ── Sub-composables ──

@Composable
private fun PlayPauseIcon(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(200),
        label = "icon_scale",
    )
    Box(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "暂停" else "播放",
            tint = Color.White.copy(alpha = 0.92f),
            modifier = Modifier.size(38.dp),
        )
    }
}

@Composable
private fun ShimmerLoadingOverlay(modifier: Modifier = Modifier) {
    val shimmerAlpha by animateFloatAsState(
        targetValue = 0.6f,
        animationSpec = tween(600),
        label = "shimmer",
    )
    Box(
        modifier = modifier.alpha(shimmerAlpha),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 2.5.dp,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Composable
private fun ErrorOverlay(
    canRetry: Boolean,
    retryCount: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(12.dp),
        )
        Text(
            text = "加载失败",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp),
        )
        if (canRetry) {
            Text(
                text = "自动重试中 ($retryCount/3)",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Text(
            text = "点击重试",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(top = 14.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.22f))
                .clickable(onClick = onRetry)
                .padding(horizontal = 28.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun PlaybackProgress(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sliderMax = durationMs.coerceAtLeast(1L).toFloat()
    val sliderValue = positionMs.coerceIn(0L, durationMs.coerceAtLeast(0L)).toFloat()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatPlaybackTime(positionMs),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
            )
            Text(
                text = formatPlaybackTime(durationMs),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..sliderMax,
            modifier = Modifier.height(36.dp), // Larger touch target
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.22f),
            ),
        )
    }
}

// Scale-in animation helper
private val scaleIn = androidx.compose.animation.scaleIn(
    initialScale = 1.4f,
    animationSpec = tween(250),
)
