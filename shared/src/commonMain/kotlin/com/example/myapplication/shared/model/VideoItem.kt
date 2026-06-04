package com.example.myapplication.shared.model

data class VideoQuality(
    val label: String,
    val url: String,
)

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
) {
    fun defaultQuality(): VideoQuality {
        return qualityUrls.firstOrNull { it.label == "720P" }
            ?: qualityUrls.firstOrNull()
            ?: VideoQuality(label = "默认", url = videoUrl)
    }

    fun defaultPlaybackUrl(): String = defaultQuality().url

    fun findQuality(targetQualityLabel: String): VideoQuality? {
        return qualityUrls.firstOrNull { it.label.equals(targetQualityLabel, ignoreCase = true) }
    }
}
