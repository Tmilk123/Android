package com.example.myapplication.model

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val authorAvatar: String,
    val videoUrl: String,
    val coverUrl: String,
    val durationText: String,
    val likeCount: String,
    val commentCount: String,
    val collectCount: String,
    val shareCount: String,
    val tags: List<String>,
    val recommendWords: List<String>,
    val qualityUrls: List<VideoQuality> = emptyList(),
)
