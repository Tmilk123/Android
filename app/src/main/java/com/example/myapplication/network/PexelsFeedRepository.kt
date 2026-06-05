package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.VideoItem
import com.example.myapplication.model.VideoQuality
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Pexels API 视频数据源
 *
 * 使用 Pexels 免费 API (200 req/h):
 *   https://www.pexels.com/api/
 *
 * 搜索关键词匹配中文内容:
 *   - 自然风景 → nature, forest, mountain, ocean
 *   - 城市生活 → city, urban, street, night
 *   - 美食 → food, cooking, coffee
 *   - 运动 → sports, running, exercise
 *   - 科技 → technology, coding, computer
 *   - 宠物 → cat, dog, pet
 *   - 旅行 → travel, beach, landscape
 *   - 艺术 → art, painting, dance
 */
class PexelsFeedRepository(private val apiKey: String) {

    private val api: PexelsApi by lazy { createApi() }

    /** 中文搜索词 → Pexels 英文查询映射 */
    private val searchQueries = listOf(
        "nature forest mountain" to listOf("自然风光", "森林", "山"),
        "ocean beach waves" to listOf("海洋", "海滩", "海浪"),
        "city urban night street" to listOf("城市", "夜景", "街拍"),
        "food cooking meal" to listOf("美食", "烹饪", "料理"),
        "sports running exercise" to listOf("运动", "跑步", "健身"),
        "technology coding computer" to listOf("科技", "编程", "数码"),
        "cat dog pet animal" to listOf("宠物", "猫咪", "狗狗"),
        "travel landscape adventure" to listOf("旅行", "风景", "探险"),
        "art painting creative" to listOf("艺术", "绘画", "创意"),
        "dance music performance" to listOf("舞蹈", "音乐", "表演"),
    )

    // ── 缓存 ──
    private var cachedVideos: List<VideoItem>? = null
    private var cachedFeedItems: List<FeedItem>? = null

    suspend fun loadFeedPage(page: Int, pageSize: Int): List<FeedItem> {
        if (page <= 0 || pageSize <= 0) return emptyList()
        val items = getOrFetchFeedItems()
        val fromIndex = (page - 1) * pageSize
        if (fromIndex >= items.size) return emptyList()
        return items.drop(fromIndex).take(pageSize)
    }

    suspend fun loadAllVideos(): List<VideoItem> {
        return getOrFetchVideos()
    }

    suspend fun searchVideos(keyword: String): List<VideoItem> {
        // 直接用关键词搜索 Pexels
        return fetchVideosForQuery(keyword).mapIndexed { index, pexelsVideo ->
            pexelsVideo.toVideoItem("search", index, listOf(keyword))
        }
    }

    fun findVideoIndexById(videoId: String): Int {
        val videos = cachedVideos ?: return -1
        return videos.indexOfFirst { it.id == videoId }
    }

    // ── 内部实现 ──

    private suspend fun getOrFetchFeedItems(): List<FeedItem> {
        cachedFeedItems?.let { return it }
        val items = mutableListOf<FeedItem>()
        val videos = getOrFetchVideos()
        videos.forEach { items.add(FeedItem.Video(it)) }
        cachedFeedItems = items
        return items
    }

    private suspend fun getOrFetchVideos(): List<VideoItem> {
        cachedVideos?.let { return it }

        val allVideos = mutableListOf<VideoItem>()
        // 用不同查询词获取多样化内容
        for ((query, tags) in searchQueries.take(10)) {
            try {
                val response = api.searchVideos(
                    apiKey = apiKey,
                    query = query,
                    perPage = 1,
                )
                response.videos.firstOrNull()?.let { pexelsVideo ->
                    allVideos.add(pexelsVideo.toVideoItem("pexels", allVideos.size, tags))
                }
            } catch (e: Exception) {
                Log.w(TAG, "Pexels query '$query' failed: ${e.message}")
            }
        }

        cachedVideos = allVideos
        Log.d(TAG, "Fetched ${allVideos.size} videos from Pexels")
        return allVideos
    }

    private suspend fun fetchVideosForQuery(query: String): List<PexelsVideo> {
        try {
            // 先试原词，失败则用英文映射
            val englishQuery = searchQueries
                .firstOrNull { (_, tags) -> tags.any { query.contains(it) } }
                ?.first ?: query
            val response = api.searchVideos(
                apiKey = apiKey,
                query = englishQuery,
                perPage = 10,
            )
            return response.videos
        } catch (e: Exception) {
            Log.w(TAG, "Search '$query' failed: ${e.message}")
            return emptyList()
        }
    }

    // ── 映射 ──

    private fun PexelsVideo.toVideoItem(
        source: String,
        index: Int,
        tags: List<String>,
    ): VideoItem {
        val bestFile = pickBestQuality()
        val author = user.name.ifBlank { "Pexels 创作者" }
        val authorInitials = if (author.contains(" ")) {
            author.split(" ").take(2).joinToString("") { it.take(1) }
        } else author.take(2)

        return VideoItem(
            id = "${source}_${id}",
            title = "${tags.firstOrNull() ?: "精彩"}瞬间",
            description = "来自 Pexels 创作者 $author 的高清素材。",
            authorName = author,
            authorAvatar = "https://ui-avatars.com/api/?name=$authorInitials&background=D81E06&color=fff&size=150",
            videoUrl = bestFile?.link.orEmpty(),
            coverUrl = image ?: videoPictures?.firstOrNull()?.picture.orEmpty(),
            durationText = formatDuration(duration),
            likeCount = formatCount(id % 10000 + 1000),
            commentCount = formatCount(id % 5000 + 100),
            collectCount = formatCount(id % 8000 + 500),
            shareCount = formatCount(id % 3000 + 200),
            tags = tags,
            recommendWords = tags,
            qualityUrls = buildQualityUrls(),
        )
    }

    private fun PexelsVideo.pickBestQuality(): PexelsVideoFile? {
        return videoFiles
            .filter { it.fileType == "video/mp4" }
            .maxByOrNull { it.width * it.height }
            ?: videoFiles.firstOrNull()
    }

    private fun PexelsVideo.buildQualityUrls(): List<VideoQuality> {
        return videoFiles
            .filter { it.fileType == "video/mp4" }
            .map { VideoQuality(label = it.quality.uppercase(), url = it.link) }
    }

    private fun formatDuration(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return "%02d:%02d".format(min, sec)
    }

    private fun formatCount(num: Long): String {
        return when {
            num >= 10000 -> "${"%.1f".format(num / 10000.0)}万"
            num >= 1000 -> "${"%.1f".format(num / 1000.0)}k"
            else -> num.toString()
        }
    }

    // ── Retrofit ──

    private fun createApi(): PexelsApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(PexelsApi.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PexelsApi::class.java)
    }

    companion object {
        private const val TAG = "PexelsRepo"
    }
}
