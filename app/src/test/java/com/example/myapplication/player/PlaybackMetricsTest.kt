package com.example.myapplication.player

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackMetricsTest {

    @Test
    fun coldStartPrepareUsesReadyMinusPrepareStart() {
        val metrics = PlaybackMetrics(
            videoId = "v001",
            videoUrl = "https://example.com/v001.mp4",
            prepareStartTimeMs = 1_000L,
            firstReadyTimeMs = 1_850L,
            firstFrameTimeMs = 1_850L,
            coldStartPrepareMs = 850L,
            preloadPrepareMs = 0L,
            isPreloaded = false,
        )

        assertEquals(850L, metrics.displayStartMs)
        assertEquals(0, metrics.improvementPercent)
    }

    @Test
    fun improvementPercentUsesColdMinusPreloadOverCold() {
        val metrics = PlaybackMetrics(
            videoId = "v002",
            videoUrl = "https://example.com/v002.mp4",
            prepareStartTimeMs = 2_000L,
            firstReadyTimeMs = 2_360L,
            firstFrameTimeMs = 2_360L,
            coldStartPrepareMs = 850L,
            preloadPrepareMs = 360L,
            isPreloaded = true,
        )

        assertEquals(360L, metrics.displayStartMs)
        assertEquals(58, metrics.improvementPercent)
    }

    @Test
    fun improvementPercentIsZeroWhenColdStartIsMissing() {
        val metrics = PlaybackMetrics(
            videoId = "v003",
            videoUrl = "https://example.com/v003.mp4",
            prepareStartTimeMs = 3_000L,
            firstReadyTimeMs = 3_300L,
            firstFrameTimeMs = 3_300L,
            coldStartPrepareMs = 0L,
            preloadPrepareMs = 300L,
            isPreloaded = true,
        )

        assertEquals(0, metrics.improvementPercent)
    }
}
