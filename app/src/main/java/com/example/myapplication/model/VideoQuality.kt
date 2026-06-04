package com.example.myapplication.model

data class VideoQuality(
    val label: String,
    val url: String,
)

fun VideoItem.defaultQuality(): VideoQuality {
    return qualityUrls.firstOrNull { it.label.equals(DEFAULT_QUALITY_LABEL, ignoreCase = true) }
        ?: qualityUrls.firstOrNull()
        ?: VideoQuality(label = FALLBACK_QUALITY_LABEL, url = videoUrl)
}

fun VideoItem.defaultPlaybackUrl(): String {
    return defaultQuality().url
}

fun VideoItem.findQuality(targetQualityLabel: String): VideoQuality? {
    return qualityUrls.firstOrNull { it.label.equals(targetQualityLabel, ignoreCase = true) }
}

private const val DEFAULT_QUALITY_LABEL = "720P"
private const val FALLBACK_QUALITY_LABEL = "默认"
