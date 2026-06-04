package com.example.myapplication.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_metrics")
data class PlaybackMetricsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "video_id")
    val videoId: String,

    @ColumnInfo(name = "video_url")
    val videoUrl: String,

    @ColumnInfo(name = "quality_label")
    val qualityLabel: String,

    @ColumnInfo(name = "cold_start_prepare_ms")
    val coldStartPrepareMs: Long,

    @ColumnInfo(name = "preload_prepare_ms")
    val preloadPrepareMs: Long,

    @ColumnInfo(name = "display_start_ms")
    val displayStartMs: Long,

    @ColumnInfo(name = "is_preloaded")
    val isPreloaded: Boolean,

    @ColumnInfo(name = "improvement_percent")
    val improvementPercent: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
