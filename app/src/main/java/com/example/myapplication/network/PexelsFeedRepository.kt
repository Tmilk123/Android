package com.example.myapplication.network

import android.content.Context
import android.util.Log
import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.ImageTextItem
import com.example.myapplication.model.VideoItem
import com.example.myapplication.model.VideoQuality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Pexels API + Unsplash 混合数据源
 *
 * URL 过期处理:
 *   - 缓存带 TTL (2小时), 过期后后台静默刷新
 *   - 单视频 URL 过期 → refreshVideoUrl() 重新获取
 */
class PexelsFeedRepository(
    private val apiKey: String,
    private val cacheDir: File? = null,
) {

    private val api: PexelsApi by lazy { createApi() }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    data class CategoryQuery(val category: String, val query: String, val tags: List<String>)

    private val categoryQueries = listOf(
        CategoryQuery("推荐", "nature scenic beautiful", listOf("自然", "风景")),
        CategoryQuery("推荐", "city life urban", listOf("城市", "生活")),
        CategoryQuery("热点", "travel adventure explore", listOf("旅行", "探险")),
        CategoryQuery("热点", "drone aerial view", listOf("航拍", "风光")),
        CategoryQuery("社会", "people street daily", listOf("社会", "街拍")),
        CategoryQuery("娱乐", "funny cute animals", listOf("搞笑", "萌宠")),
        CategoryQuery("娱乐", "music dance party", listOf("音乐", "舞蹈")),
        CategoryQuery("科技", "technology coding computer", listOf("科技", "数码")),
        CategoryQuery("体育", "sports running fitness", listOf("运动", "健身")),
        CategoryQuery("美食", "food cooking delicious", listOf("美食", "料理")),
    )

    private val unsplashImages = listOf(
        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=720&h=1280&fit=crop",
        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=720&h=1280&fit=crop",
        "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=720&h=1280&fit=crop",
        "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=720&h=1280&fit=crop",
        "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=720&h=1280&fit=crop",
        "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=720&h=1280&fit=crop",
    )

    // ── 缓存状态 ──
    @Volatile private var cachedFeedItems: List<FeedItem>? = null
    @Volatile private var cachedVideos: List<VideoItem>? = null
    @Volatile private var fetchedAt: Long = 0L
    private var isRefreshing = false

    companion object {
        private const val TAG = "PexelsRepo"
        /** 缓存有效期: 2 小时 (Pexels 签名 URL 通常 2-4 小时过期) */
        private const val CACHE_TTL_MS = 2 * 60 * 60 * 1000L
    }

    // ── Public API ──

    suspend fun loadFeedPage(page: Int, pageSize: Int): List<FeedItem> {
        if (page <= 0 || pageSize <= 0) return emptyList()
        val items = getOrFetchFeedItems()
        val from = (page - 1) * pageSize
        if (from >= items.size) return emptyList()
        return items.drop(from).take(pageSize)
    }

    suspend fun loadFeedByCategory(category: String, page: Int, pageSize: Int): List<FeedItem> {
        if (category == "推荐") return loadFeedPage(page, pageSize)
        val items = getOrFetchFeedItems()
            .filter { item ->
                val tags = when (item) {
                    is FeedItem.Video -> item.item.tags
                    is FeedItem.ImageText -> item.item.tags
                }
                categoryQueries.any { cq -> cq.category == category && cq.tags.any { it in tags } }
            }
        val from = (page - 1) * pageSize
        if (from >= items.size) return emptyList()
        return items.drop(from).take(pageSize)
    }

    suspend fun loadAllVideos(): List<VideoItem> = getOrFetchVideos()

    suspend fun searchVideos(keyword: String): List<VideoItem> {
        return try {
            val resp = api.searchVideos(apiKey, keyword, 10)
            resp.videos.mapIndexed { i, v -> v.toVideoItem("search", i, listOf(keyword)) }
        } catch (e: Exception) { Log.w(TAG, "Search: ${e.message}"); emptyList() }
    }

    /**
     * 单个视频 URL 过期 → 用相同关键词重新查询 API 获取新 URL
     */
    suspend fun refreshVideoUrl(videoId: String): VideoItem? {
        val oldVideo = cachedVideos?.firstOrNull { it.id == videoId } ?: return null
        val query = categoryQueries
            .firstOrNull { cq -> cq.tags.any { it in oldVideo.tags } }
            ?: return null

        return try {
            val resp = api.searchVideos(apiKey, query.query, perPage = 3)
            // 找到内容不同的新视频 URL
            val fresh = resp.videos.firstOrNull { it.id.toString() != videoId.removePrefix("pexels_") }
                ?: resp.videos.firstOrNull()
                ?: return null

            val newVideo = fresh.toVideoItem("pexels", query.tags.hashCode(), query.tags)
            // 更新缓存
            cachedVideos = cachedVideos?.map { if (it.id == videoId) newVideo else it }
            cachedFeedItems = cachedFeedItems?.map { item ->
                if (item is FeedItem.Video && item.item.id == videoId)
                    FeedItem.Video(newVideo) else item
            }
            Log.d(TAG, "Refreshed URL for $videoId → ${newVideo.videoUrl.take(60)}...")
            newVideo
        } catch (e: Exception) { Log.w(TAG, "Refresh $videoId failed: ${e.message}"); null }
    }

    fun findVideoIndexById(videoId: String): Int {
        return (cachedVideos ?: emptyList()).indexOfFirst { it.id == videoId }
    }

    fun clearCache() {
        cachedFeedItems = null
        cachedVideos = null
        fetchedAt = 0L
    }

    // ── Internal ──

    private suspend fun getOrFetchFeedItems(): List<FeedItem> {
        cachedFeedItems?.let { return it }
        val items = mutableListOf<FeedItem>()
        val videos = getOrFetchVideos()
        videos.forEachIndexed { index, video ->
            items.add(FeedItem.Video(video))
            if ((index + 1) % 3 == 0 && index < unsplashImages.size) {
                items.add(createImageItem(index, video))
            }
        }
        cachedFeedItems = items
        return items
    }

    private suspend fun getOrFetchVideos(): List<VideoItem> {
        val now = System.currentTimeMillis()
        val cacheAge = now - fetchedAt

        // 缓存未过期 → 直接返回
        if (cachedVideos != null && cacheAge < CACHE_TTL_MS) {
            return cachedVideos!!
        }

        // 缓存过期但可用 → 返回旧缓存 + 后台刷新
        if (cachedVideos != null && cacheAge >= CACHE_TTL_MS && !isRefreshing) {
            Log.d(TAG, "Cache expired (${cacheAge / 60000}min), refreshing in background")
            scope.launch { refreshAllVideos() }
            return cachedVideos!!
        }

        // 无缓存或正在刷新中 → 阻塞获取
        return refreshAllVideos()
    }

    private suspend fun refreshAllVideos(): List<VideoItem> {
        isRefreshing = true
        try {
            val results = coroutineScope {
                categoryQueries.map { cq ->
                    async {
                        try {
                            val resp = api.searchVideos(apiKey, cq.query, perPage = 1)
                            resp.videos.firstOrNull()?.toVideoItem("pexels", cq.tags.hashCode(), cq.tags)
                        } catch (e: Exception) {
                            Log.w(TAG, "'${cq.query}' failed: ${e.message}")
                            null
                        }
                    }
                }.awaitAll()
            }

            val videos = results.filterNotNull()
            cachedVideos = videos
            cachedFeedItems = null  // 强制重建 FeedItem 列表
            fetchedAt = System.currentTimeMillis()
            Log.d(TAG, "Fetched ${videos.size}/${categoryQueries.size} videos (fresh, parallel)")
            return videos
        } finally {
            isRefreshing = false
        }
    }

    // ── Image Text ──

    private fun createImageItem(index: Int, prevVideo: VideoItem): FeedItem.ImageText {
        val i = index % unsplashImages.size
        val urls = listOf(unsplashImages[i], unsplashImages[(i + 1) % 6], unsplashImages[(i + 2) % 6])
        return FeedItem.ImageText(ImageTextItem(
            id = "pexels_img_$index",
            title = "${prevVideo.tags.firstOrNull() ?: "精彩"}图集",
            description = "高清摄影，记录${prevVideo.tags.joinToString("、")}的精彩瞬间。",
            authorName = prevVideo.authorName,
            authorAvatar = prevVideo.authorAvatar,
            imageUrl = urls.first(),
            imageUrls = urls,
            likeCount = prevVideo.likeCount, commentCount = prevVideo.commentCount,
            collectCount = prevVideo.collectCount, shareCount = prevVideo.shareCount,
            tags = prevVideo.tags, recommendWords = prevVideo.recommendWords,
        ))
    }

    // ── Mapping ──

    private fun PexelsVideo.toVideoItem(source: String, index: Int, tags: List<String>): VideoItem {
        val best = videoFiles.filter { it.fileType == "video/mp4" }
            .maxByOrNull { it.width * it.height } ?: videoFiles.firstOrNull()
        val author = user.name.ifBlank { "Pexels 创作者" }
        val initials = author.split(" ").take(2).joinToString("") { it.take(1) }.ifBlank { author.take(2) }

        return VideoItem(
            id = "${source}_${id}",
            title = "${tags.firstOrNull() ?: "精彩"}瞬间",
            description = "Pexels 创作者 $author 的高清素材。",
            authorName = author,
            authorAvatar = "https://ui-avatars.com/api/?name=$initials&background=D81E06&color=fff&size=150",
            videoUrl = best?.link.orEmpty(),
            coverUrl = image ?: videoPictures?.firstOrNull()?.picture.orEmpty(),
            durationText = formatDuration(duration),
            likeCount = formatCount(id % 10000 + 1000),
            commentCount = formatCount(id % 5000 + 100),
            collectCount = formatCount(id % 8000 + 500),
            shareCount = formatCount(id % 3000 + 200),
            tags = tags, recommendWords = tags,
            qualityUrls = videoFiles.filter { it.fileType == "video/mp4" }
                .map { VideoQuality(it.quality.uppercase(), it.link) },
        )
    }

    private fun formatDuration(s: Int) = "%02d:%02d".format(s / 60, s % 60)
    private fun formatCount(n: Long) = when {
        n >= 10000 -> "${"%.1f".format(n / 10000.0)}万"
        n >= 1000 -> "${"%.1f".format(n / 1000.0)}k"
        else -> n.toString()
    }

    private fun createApi(): PexelsApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(PexelsApi.BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(PexelsApi::class.java)
    }
}
