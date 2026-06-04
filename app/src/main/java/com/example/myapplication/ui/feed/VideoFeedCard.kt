package com.example.myapplication.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    modifier: Modifier = Modifier,
) {
    val playerState = playerManager.state
    val isCurrentVideo = playerState.videoId == item.id
    val context = LocalContext.current
    val fullscreenController = remember(context) {
        context.findActivity()?.let { FullscreenController(it) }
    }
    var isQualityMenuOpen by remember(item.id) { mutableStateOf(false) }
    var isLandscapeFullscreen by remember(item.id) { mutableStateOf(false) }

    LaunchedEffect(isActive, item.videoUrl) {
        if (isActive) {
            playerManager.play(item)
        } else if (isCurrentVideo) {
            playerManager.pause()
        }
    }

    LaunchedEffect(isActive, item.videoUrl) {
        while (isActive) {
            playerManager.refreshProgress()
            delay(500)
        }
    }

    DisposableEffect(fullscreenController) {
        onDispose {
            fullscreenController?.exitLandscapeFullscreen()
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable(enabled = isActive) {
                playerManager.togglePlayPause()
            },
    ) {
        if (isActive) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        player = playerManager.player
                    }
                },
                update = { view ->
                    view.player = playerManager.player
                },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AsyncImage(
                model = item.coverUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.16f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.72f),
                        )
                    )
                )
        )

        when {
            isCurrentVideo && playerState.isLoading -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            isCurrentVideo && playerState.hasError -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "视频加载失败",
                        color = Color.White,
                        fontSize = 16.sp,
                    )
                    if (playerState.canRetry) {
                        Text(
                            text = "重试 (${playerState.retryCount}/${3})",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Text(
                        text = "点击重试",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable { playerManager.retry() }
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                    )
                }
            }

            isCurrentVideo -> {
                PlaybackStatusIcon(
                    isPlaying = playerState.isPlaying,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        if (isCurrentVideo) {
            if (isLandscapeFullscreen) {
                Text(
                    text = "返回竖屏",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 24.dp, start = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.52f))
                        .clickable {
                            isLandscapeFullscreen = false
                            fullscreenController?.exitLandscapeFullscreen()
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                )
            } else {
                Text(
                    text = "横屏",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 150.dp, end = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.48f))
                        .clickable {
                            isLandscapeFullscreen = true
                            fullscreenController?.enterLandscapeFullscreen()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }

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
                                text = { Text(text = quality.label) },
                                onClick = {
                                    isQualityMenuOpen = false
                                    playerManager.switchQuality(item, quality.label)
                                },
                            )
                        }
                    }
                }
            }

            PlaybackProgress(
                positionMs = playerState.positionMs,
                durationMs = playerState.durationMs,
                onSeek = playerManager::seekTo,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 12.dp, end = 12.dp, bottom = 6.dp),
            )
        }
    }
}

@Composable
private fun PlaybackStatusIcon(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.30f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isPlaying) "暂停" else "播放",
            color = Color.White,
            fontSize = 16.sp,
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
                fontSize = 12.sp,
            )
            Text(
                text = formatPlaybackTime(durationMs),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..sliderMax,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.28f),
            ),
        )
    }
}
