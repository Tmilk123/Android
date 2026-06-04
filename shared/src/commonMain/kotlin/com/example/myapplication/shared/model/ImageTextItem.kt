package com.example.myapplication.shared.model

data class ImageTextItem(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val authorAvatar: String,
    val imageUrl: String,
    val likeCount: String,
    val commentCount: String,
    val collectCount: String,
    val shareCount: String,
    val tags: List<String>,
    val recommendWords: List<String>,
)
