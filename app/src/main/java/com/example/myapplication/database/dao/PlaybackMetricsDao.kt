package com.example.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.database.entity.PlaybackMetricsEntity

@Dao
interface PlaybackMetricsDao {

    @Insert
    suspend fun insertMetrics(metrics: PlaybackMetricsEntity)

    @Query("SELECT * FROM playback_metrics ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecentMetrics(limit: Int = 50): List<PlaybackMetricsEntity>

    @Query("SELECT * FROM playback_metrics ORDER BY created_at DESC")
    suspend fun getAllMetrics(): List<PlaybackMetricsEntity>

    @Query(
        """
        SELECT
            COUNT(*) as totalCount,
            AVG(cold_start_prepare_ms) as avgColdStartMs,
            AVG(preload_prepare_ms) as avgPreloadMs,
            AVG(display_start_ms) as avgDisplayMs,
            AVG(improvement_percent) as avgImprovementPct,
            CAST(SUM(CASE WHEN is_preloaded = 1 THEN 1 ELSE 0 END) AS REAL) / COUNT(*) * 100 as preloadHitRate
        FROM playback_metrics
        """
    )
    suspend fun getAggregateStats(): MetricsAggregateStats

    @Query("DELETE FROM playback_metrics")
    suspend fun clearAll()
}

data class MetricsAggregateStats(
    val totalCount: Int,
    val avgColdStartMs: Double,
    val avgPreloadMs: Double,
    val avgDisplayMs: Double,
    val avgImprovementPct: Double,
    val preloadHitRate: Double,
)
