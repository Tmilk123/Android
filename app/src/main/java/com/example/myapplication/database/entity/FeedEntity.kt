package com.example.myapplication.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_cache")
data class FeedEntity(
    @PrimaryKey
    val id: String,
    val itemType: String,
    val title: String,
    val description: String,
    val authorName: String,
    val authorAvatar: String,
    val videoUrl: String?,
    val qualityUrlsJson: String?,
    val coverUrl: String?,
    val imageUrl: String?,
    val durationText: String?,
    val likeCount: String,
    val commentCount: String,
    val collectCount: String,
    val shareCount: String,
    val tagsJson: String,
    val recommendWordsJson: String,
    val page: Int,
    val cachedAt: Long,
)
