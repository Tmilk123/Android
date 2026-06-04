package com.example.myapplication.data

import com.example.myapplication.database.dao.MetricsAggregateStats
import com.example.myapplication.database.dao.PlaybackMetricsDao
import com.example.myapplication.database.entity.PlaybackMetricsEntity
import com.example.myapplication.player.PlaybackMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MetricsRepository(
    private val dao: PlaybackMetricsDao,
) {

    suspend fun recordMetrics(metrics: PlaybackMetrics, qualityLabel: String) {
        withContext(Dispatchers.IO) {
            dao.insertMetrics(
                PlaybackMetricsEntity(
                    videoId = metrics.videoId,
                    videoUrl = metrics.videoUrl,
                    qualityLabel = qualityLabel,
                    coldStartPrepareMs = metrics.coldStartPrepareMs,
                    preloadPrepareMs = metrics.preloadPrepareMs,
                    displayStartMs = metrics.displayStartMs,
                    isPreloaded = metrics.isPreloaded,
                    improvementPercent = metrics.improvementPercent,
                )
            )
        }
    }

    suspend fun getAggregateStats(): MetricsAggregateStats {
        return withContext(Dispatchers.IO) {
            dao.getAggregateStats()
        }
    }

    suspend fun getRecentMetrics(limit: Int = 50): List<PlaybackMetricsEntity> {
        return withContext(Dispatchers.IO) {
            dao.getRecentMetrics(limit)
        }
    }

    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            dao.clearAll()
        }
    }
}
