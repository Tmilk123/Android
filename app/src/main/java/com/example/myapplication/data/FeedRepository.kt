package com.example.myapplication.data

import com.example.myapplication.database.dao.FeedDao
import com.example.myapplication.database.entity.FeedEntity
import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.ImageTextItem
import com.example.myapplication.model.VideoQuality
import com.example.myapplication.model.VideoItem
import com.example.myapplication.network.PexelsFeedRepository
import org.json.JSONArray
import org.json.JSONObject

data class CachedFeedSnapshot(
    val items: List<FeedItem>,
    val lastPage: Int,
)

class FeedRepository(
    private val fakeFeedRepository: FakeFeedRepository,
    private val feedDao: FeedDao,
) {
    private val pexelsRepository: PexelsFeedRepository?
        get() = AppConfig.pexelsRepository

    suspend fun loadCachedFeed(): CachedFeedSnapshot {
        val cachedEntities = feedDao.getAllCachedFeed()
        return CachedFeedSnapshot(
            items = cachedEntities.mapNotNull { it.toFeedItem() },
            lastPage = feedDao.getMaxCachedPage(),
        )
    }

    suspend fun loadFeedPage(page: Int, pageSize: Int): List<FeedItem> {
        val pexels = pexelsRepository
        val items = when {
            AppConfig.dataSource == "pexels" && pexels != null ->
                pexels.loadFeedPage(page, pageSize)
            else ->
                fakeFeedRepository.loadFeedPage(page = page, pageSize = pageSize)
        }
        if (items.isNotEmpty()) {
            feedDao.insertFeedItems(
                items.mapIndexed { index, item ->
                    item.toEntity(
                        page = page,
                        cachedAt = System.currentTimeMillis() + index,
                    )
                }
            )
        }
        return items
    }

    suspend fun searchVideos(keyword: String): List<VideoItem> {
        val pexels = pexelsRepository
        return if (AppConfig.dataSource == "pexels" && pexels != null) {
            pexels.searchVideos(keyword)
        } else {
            fakeFeedRepository.searchVideos(keyword)
        }
    }

    suspend fun refreshVideoUrl(videoId: String): VideoItem? {
        val pexels = pexelsRepository ?: return null
        return pexels.refreshVideoUrl(videoId)
    }

    suspend fun findVideoIndexById(videoId: String): Int {
        val pexels = pexelsRepository
        return if (AppConfig.dataSource == "pexels" && pexels != null) {
            pexels.findVideoIndexById(videoId)
        } else {
            fakeFeedRepository.findVideoIndexById(videoId)
        }
    }

    // ── Entity mapping (unchanged) ──

    private fun FeedItem.toEntity(page: Int, cachedAt: Long): FeedEntity {
        return when (this) {
            is FeedItem.Video -> FeedEntity(
                id = item.id,
                itemType = ITEM_TYPE_VIDEO,
                title = item.title,
                description = item.description,
                authorName = item.authorName,
                authorAvatar = item.authorAvatar,
                videoUrl = item.videoUrl,
                qualityUrlsJson = item.qualityUrls.toQualityJsonArrayString(),
                coverUrl = item.coverUrl,
                imageUrl = null,
                durationText = item.durationText,
                likeCount = item.likeCount,
                commentCount = item.commentCount,
                collectCount = item.collectCount,
                shareCount = item.shareCount,
                tagsJson = item.tags.toJsonArrayString(),
                recommendWordsJson = item.recommendWords.toJsonArrayString(),
                page = page,
                cachedAt = cachedAt,
            )
            is FeedItem.ImageText -> FeedEntity(
                id = item.id,
                itemType = ITEM_TYPE_IMAGE_TEXT,
                title = item.title,
                description = item.description,
                authorName = item.authorName,
                authorAvatar = item.authorAvatar,
                videoUrl = null,
                qualityUrlsJson = null,
                coverUrl = null,
                imageUrl = item.imageUrl,
                durationText = null,
                likeCount = item.likeCount,
                commentCount = item.commentCount,
                collectCount = item.collectCount,
                shareCount = item.shareCount,
                tagsJson = item.tags.toJsonArrayString(),
                recommendWordsJson = item.recommendWords.toJsonArrayString(),
                page = page,
                cachedAt = cachedAt,
            )
        }
    }

    private fun FeedEntity.toFeedItem(): FeedItem? {
        return when (itemType) {
            ITEM_TYPE_VIDEO -> FeedItem.Video(
                VideoItem(
                    id = id,
                    title = title,
                    description = description,
                    authorName = authorName,
                    authorAvatar = authorAvatar,
                    videoUrl = videoUrl.orEmpty(),
                    qualityUrls = qualityUrlsJson.orEmpty().toVideoQualityList(),
                    coverUrl = coverUrl.orEmpty(),
                    durationText = durationText.orEmpty(),
                    likeCount = likeCount,
                    commentCount = commentCount,
                    collectCount = collectCount,
                    shareCount = shareCount,
                    tags = tagsJson.toStringList(),
                    recommendWords = recommendWordsJson.toStringList(),
                )
            )
            ITEM_TYPE_IMAGE_TEXT -> FeedItem.ImageText(
                ImageTextItem(
                    id = id,
                    title = title,
                    description = description,
                    authorName = authorName,
                    authorAvatar = authorAvatar,
                    imageUrl = imageUrl.orEmpty(),
                    likeCount = likeCount,
                    commentCount = commentCount,
                    collectCount = collectCount,
                    shareCount = shareCount,
                    tags = tagsJson.toStringList(),
                    recommendWords = recommendWordsJson.toStringList(),
                )
            )
            else -> null
        }
    }

    private fun List<String>.toJsonArrayString(): String = JSONArray(this).toString()

    private fun List<VideoQuality>.toQualityJsonArrayString(): String {
        val array = JSONArray()
        forEach { quality ->
            array.put(JSONObject().put("label", quality.label).put("url", quality.url))
        }
        return array.toString()
    }

    private fun String.toVideoQualityList(): List<VideoQuality> {
        return runCatching {
            val array = JSONArray(this)
            List(array.length()) { index ->
                val item = array.optJSONObject(index)
                VideoQuality(
                    label = item?.optString("label").orEmpty(),
                    url = item?.optString("url").orEmpty(),
                )
            }.filter { it.label.isNotBlank() && it.url.isNotBlank() }
        }.getOrDefault(emptyList())
    }

    private fun String.toStringList(): List<String> {
        return runCatching {
            val array = JSONArray(this)
            List(array.length()) { index -> array.optString(index) }
                .filter { it.isNotBlank() }
        }.getOrDefault(emptyList())
    }

    private companion object {
        const val ITEM_TYPE_VIDEO = "video"
        const val ITEM_TYPE_IMAGE_TEXT = "image_text"
    }
}
