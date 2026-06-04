package com.example.myapplication.data

import com.example.myapplication.model.VideoItem

/**
 * Android-specific wrapper around the KMP shared [com.example.myapplication.shared.search.SearchRanker].
 *
 * The shared module (Kotlin Multiplatform) contains the canonical search ranking logic.
 * This wrapper converts between Android model types and shared model types, allowing
 * the same ranking algorithm to be reused across Android and iOS without modification.
 */
data class SearchRankedVideo(
    val video: VideoItem,
    val score: Int,
    val matchedWords: List<String>,
)

class SearchRanker {

    private val sharedRanker = com.example.myapplication.shared.search.SearchRanker()

    fun searchVideos(
        videos: List<VideoItem>,
        keyword: String,
    ): List<SearchRankedVideo> {
        // Convert Android model -> shared model
        val sharedVideos = videos.map { it.toSharedVideoItem() }

        // Delegate to KMP shared module (cross-platform, also runs on iOS)
        val sharedResults = sharedRanker.searchVideos(sharedVideos, keyword)

        // Convert shared model -> Android model
        return sharedResults.map { result ->
            SearchRankedVideo(
                video = result.video.toAndroidVideoItem(),
                score = result.score,
                matchedWords = result.matchedWords,
            )
        }
    }
}

// ---- Model mappers (Android <-> Shared) ----
// These would be auto-generated in a real project; kept explicit here for clarity.

private fun VideoItem.toSharedVideoItem(): com.example.myapplication.shared.model.VideoItem {
    return com.example.myapplication.shared.model.VideoItem(
        id = id,
        title = title,
        description = description,
        authorName = authorName,
        authorAvatar = authorAvatar,
        videoUrl = videoUrl,
        coverUrl = coverUrl,
        durationText = durationText,
        likeCount = likeCount,
        commentCount = commentCount,
        collectCount = collectCount,
        shareCount = shareCount,
        tags = tags,
        recommendWords = recommendWords,
        qualityUrls = qualityUrls.map {
            com.example.myapplication.shared.model.VideoQuality(label = it.label, url = it.url)
        },
    )
}

private fun com.example.myapplication.shared.model.VideoItem.toAndroidVideoItem(): VideoItem {
    return VideoItem(
        id = id,
        title = title,
        description = description,
        authorName = authorName,
        authorAvatar = authorAvatar,
        videoUrl = videoUrl,
        coverUrl = coverUrl,
        durationText = durationText,
        likeCount = likeCount,
        commentCount = commentCount,
        collectCount = collectCount,
        shareCount = shareCount,
        tags = tags,
        recommendWords = recommendWords,
        qualityUrls = qualityUrls.map {
            com.example.myapplication.model.VideoQuality(label = it.label, url = it.url)
        },
    )
}
