package com.example.myapplication.player

import kotlin.math.roundToInt

data class PlaybackMetrics(
    val videoId: String,
    val videoUrl: String,
    val prepareStartTimeMs: Long,
    val firstReadyTimeMs: Long,
    val firstFrameTimeMs: Long,
    val coldStartPrepareMs: Long,
    val preloadPrepareMs: Long,
    val isPreloaded: Boolean,
) {
    val displayStartMs: Long
        get() = if (isPreloaded) preloadPrepareMs else coldStartPrepareMs

    val improvementPercent: Int
        get() {
            if (!isPreloaded || coldStartPrepareMs <= 0L) return 0
            val improvement = (coldStartPrepareMs - preloadPrepareMs).coerceAtLeast(0L)
            return ((improvement * 100f) / coldStartPrepareMs).roundToInt()
        }
}
