package com.example.myapplication.data

import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.RecommendWord

/**
 * Android-specific wrapper around the KMP shared [com.example.myapplication.shared.search.RecommendWordEngine].
 *
 * The shared module (Kotlin Multiplatform) contains the canonical recommend word generation logic.
 * This wrapper converts between Android model types and shared model types, allowing
 * the same recommendation algorithm to be reused across Android and iOS without modification.
 */
class RecommendWordEngine(
    private val hotWords: List<String> = defaultHotWords,
) {

    private val sharedEngine = com.example.myapplication.shared.search.RecommendWordEngine(hotWords)

    fun buildRecommendWords(item: FeedItem, limit: Int = DEFAULT_LIMIT): List<RecommendWord> {
        // Convert Android model -> shared model
        val sharedItem = item.toSharedFeedItem()

        // Delegate to KMP shared module (cross-platform, also runs on iOS)
        val sharedResults = sharedEngine.buildRecommendWords(sharedItem, limit)

        // Convert shared model -> Android model
        return sharedResults.map { result ->
            RecommendWord(
                word = result.word,
                source = result.source,
                score = result.score,
                reason = result.reason,
            )
        }
    }

    companion object {
        const val DEFAULT_LIMIT = 5
        val defaultHotWords = com.example.myapplication.shared.search.RecommendWordEngine.defaultHotWords
    }
}

// ---- Model mappers (Android <-> Shared) ----

private fun FeedItem.toSharedFeedItem(): com.example.myapplication.shared.model.FeedItem {
    return when (this) {
        is FeedItem.Video -> com.example.myapplication.shared.model.FeedItem.Video(
            com.example.myapplication.shared.model.VideoItem(
                id = item.id,
                title = item.title,
                description = item.description,
                authorName = item.authorName,
                authorAvatar = item.authorAvatar,
                videoUrl = item.videoUrl,
                coverUrl = item.coverUrl,
                durationText = item.durationText,
                likeCount = item.likeCount,
                commentCount = item.commentCount,
                collectCount = item.collectCount,
                shareCount = item.shareCount,
                tags = item.tags,
                recommendWords = item.recommendWords,
                qualityUrls = item.qualityUrls.map {
                    com.example.myapplication.shared.model.VideoQuality(label = it.label, url = it.url)
                },
            )
        )
        is FeedItem.ImageText -> com.example.myapplication.shared.model.FeedItem.ImageText(
            com.example.myapplication.shared.model.ImageTextItem(
                id = item.id,
                title = item.title,
                description = item.description,
                authorName = item.authorName,
                authorAvatar = item.authorAvatar,
                imageUrl = item.imageUrl,
                likeCount = item.likeCount,
                commentCount = item.commentCount,
                collectCount = item.collectCount,
                shareCount = item.shareCount,
                tags = item.tags,
                recommendWords = item.recommendWords,
            )
        )
    }
}
