package com.example.myapplication.model

data class ImageTextItem(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val authorAvatar: String,
    val imageUrl: String,
    val imageUrls: List<String> = emptyList(),  // 多图支持
    val likeCount: String,
    val commentCount: String,
    val collectCount: String,
    val shareCount: String,
    val tags: List<String>,
    val recommendWords: List<String>,
) {
    /** 所有待展示的图片列表 (含主图) */
    val allImageUrls: List<String>
        get() = if (imageUrls.isNotEmpty()) imageUrls else listOf(imageUrl)
}
