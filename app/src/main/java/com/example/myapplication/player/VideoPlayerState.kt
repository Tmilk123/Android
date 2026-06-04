package com.example.myapplication.player

data class VideoPlayerState(
    val videoId: String? = null,
    val videoUrl: String? = null,
    val qualityLabel: String? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val canRetry: Boolean = false,
    val retryCount: Int = 0,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
)

fun formatPlaybackTime(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
