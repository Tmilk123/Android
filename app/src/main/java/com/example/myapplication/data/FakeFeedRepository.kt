package com.example.myapplication.data

import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.ImageTextItem
import com.example.myapplication.model.VideoQuality
import com.example.myapplication.model.VideoItem

class FakeFeedRepository(
    val useRealData: Boolean = false,
) {

    private val hotWords = listOf("今日头条", "旅行", "美食", "科技", "电影")

    /** 真实数据源 (启用时使用) */
    private val realFeedItems: List<FeedItem> by lazy {
        RealVideoDataSource.toFeedItems().map { it.withExpandedRecommendWords() }
    }

    private val realVideoItems: List<VideoItem>
        get() = realFeedItems.mapNotNull { (it as? FeedItem.Video)?.item }

    private val testVideoUrls = listOf(
        "https://media.w3.org/2010/05/sintel/trailer.mp4",
        "https://media.w3.org/2010/05/bunny/trailer.mp4",
        "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
        "https://www.w3schools.com/html/mov_bbb.mp4",
        "https://vjs.zencdn.net/v/oceans.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
    )

    private val testImageUrls = listOf(
        "https://picsum.photos/seed/travel/720/1280",
        "https://picsum.photos/seed/food/720/1280",
        "https://picsum.photos/seed/photo/720/1280",
        "https://picsum.photos/seed/tech/720/1280",
        "https://picsum.photos/seed/study/720/1280",
        "https://picsum.photos/seed/movie/720/1280"
    )

    private val testAvatarUrls = listOf(
        "https://i.pravatar.cc/150?img=1",
        "https://i.pravatar.cc/150?img=2",
        "https://i.pravatar.cc/150?img=3",
        "https://i.pravatar.cc/150?img=4",
        "https://i.pravatar.cc/150?img=5",
        "https://i.pravatar.cc/150?img=6",
        "https://i.pravatar.cc/150?img=7",
        "https://i.pravatar.cc/150?img=8",
        "https://i.pravatar.cc/150?img=9",
        "https://i.pravatar.cc/150?img=10",
        "https://i.pravatar.cc/150?img=11",
        "https://i.pravatar.cc/150?img=12",
        "https://i.pravatar.cc/150?img=13",
        "https://i.pravatar.cc/150?img=14",
        "https://i.pravatar.cc/150?img=15",
        "https://i.pravatar.cc/150?img=16"
    )

    private val feedItems: List<FeedItem> = listOf(
        FeedItem.Video(
            VideoItem(
                id = "video_travel_01",
                title = "海边旅行的一天",
                description = "跟着镜头走过海岸线和落日沙滩。",
                authorName = "小鹿旅行记",
                authorAvatar = testAvatarUrls[0],
                videoUrl = testVideoUrls[0],
                coverUrl = testImageUrls[0],
                durationText = "09:56",
                likeCount = "12.8万",
                commentCount = "4321",
                collectCount = "1.2万",
                shareCount = "3200",
                tags = listOf("旅行", "海边"),
                recommendWords = listOf("周末旅行", "城市漫步")
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_food_02",
                title = "夜市美食三连吃",
                description = "烤串、小面和甜品，一次逛完热闹夜市。",
                authorName = "阿诚探店",
                authorAvatar = testAvatarUrls[1],
                videoUrl = testVideoUrls[1],
                coverUrl = testImageUrls[1],
                durationText = "10:17",
                likeCount = "9.6万",
                commentCount = "2800",
                collectCount = "8600",
                shareCount = "2100",
                tags = listOf("美食", "探店"),
                recommendWords = listOf("夜市小吃", "本地味道", "街头美食")
            )
        ),
        FeedItem.ImageText(
            ImageTextItem(
                id = "image_photo_01",
                title = "雨后街头摄影",
                description = "水面反光让普通街道有了电影感。",
                authorName = "光影手账",
                authorAvatar = testAvatarUrls[2],
                imageUrl = testImageUrls[2],
                likeCount = "3.4万",
                commentCount = "920",
                collectCount = "4500",
                shareCount = "800",
                tags = listOf("摄影", "街拍"),
                recommendWords = listOf("构图技巧", "雨天拍照"),
                imageUrls = listOf(testImageUrls[2], testImageUrls[3], testImageUrls[0])
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_tech_03",
                title = "折叠屏手机体验",
                description = "从外屏操作到多任务窗口，看新设备的真实使用感。",
                authorName = "数码观察员",
                authorAvatar = testAvatarUrls[3],
                videoUrl = testVideoUrls[2],
                coverUrl = testImageUrls[3],
                durationText = "00:31",
                likeCount = "7.2万",
                commentCount = "1900",
                collectCount = "6200",
                shareCount = "1500",
                tags = listOf("科技", "数码"),
                recommendWords = listOf("折叠屏", "手机评测", "效率工具")
            )
        ),
        FeedItem.ImageText(
            ImageTextItem(
                id = "image_study_02",
                title = "学习笔记整理法",
                description = "用三栏结构记录重点、问题和复盘。",
                authorName = "自习室同学",
                authorAvatar = testAvatarUrls[4],
                imageUrl = testImageUrls[4],
                likeCount = "2.1万",
                commentCount = "610",
                collectCount = "7000",
                shareCount = "560",
                tags = listOf("学习", "笔记"),
                recommendWords = listOf("学习方法", "知识管理"),
                imageUrls = listOf(testImageUrls[4], testImageUrls[2], testImageUrls[5])
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_movie_04",
                title = "三分钟看懂经典电影镜头",
                description = "拆解光线、调度和配乐如何推动情绪。",
                authorName = "片场放映员",
                authorAvatar = testAvatarUrls[5],
                videoUrl = testVideoUrls[7],
                coverUrl = testImageUrls[5],
                durationText = "14:48",
                likeCount = "15.3万",
                commentCount = "5400",
                collectCount = "2.4万",
                shareCount = "4600",
                tags = listOf("电影", "镜头语言"),
                recommendWords = listOf("经典片段", "电影解析", "配乐")
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_music_05",
                title = "地铁口的即兴音乐",
                description = "吉他和键盘合奏，让下班路变得轻快。",
                authorName = "街角旋律",
                authorAvatar = testAvatarUrls[6],
                videoUrl = testVideoUrls[3],
                coverUrl = testImageUrls[0],
                durationText = "01:01",
                likeCount = "6.8万",
                commentCount = "1730",
                collectCount = "5100",
                shareCount = "940",
                tags = listOf("音乐", "城市"),
                recommendWords = listOf("即兴演奏", "街头表演")
            )
        ),
        FeedItem.ImageText(
            ImageTextItem(
                id = "image_city_03",
                title = "城市通勤观察",
                description = "早高峰里的秩序、速度和烟火气。",
                authorName = "城市记录员",
                authorAvatar = testAvatarUrls[7],
                imageUrl = testImageUrls[1],
                likeCount = "4.5万",
                commentCount = "1300",
                collectCount = "3900",
                shareCount = "760",
                tags = listOf("城市", "生活"),
                recommendWords = listOf("通勤", "街道观察"),
                imageUrls = listOf(testImageUrls[1], testImageUrls[3], testImageUrls[4])
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_city_06",
                title = "城市天台日落",
                description = "在高处看晚霞掠过楼群，记录一天结束的光。",
                authorName = "楼顶日记",
                authorAvatar = testAvatarUrls[8],
                videoUrl = testVideoUrls[4],
                coverUrl = testImageUrls[2],
                durationText = "02:54",
                likeCount = "8.1万",
                commentCount = "2010",
                collectCount = "7400",
                shareCount = "1300",
                tags = listOf("日落", "生活"),
                recommendWords = listOf("天台风景", "晚霞")
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_sports_07",
                title = "十分钟晨跑热身",
                description = "跑前激活脚踝、膝盖和核心，降低受伤风险。",
                authorName = "运动研究所",
                authorAvatar = testAvatarUrls[9],
                videoUrl = testVideoUrls[5],
                coverUrl = testImageUrls[3],
                durationText = "01:02",
                likeCount = "5.7万",
                commentCount = "980",
                collectCount = "8300",
                shareCount = "640",
                tags = listOf("运动", "健身"),
                recommendWords = listOf("晨跑计划")
            )
        ),
        FeedItem.ImageText(
            ImageTextItem(
                id = "image_food_04",
                title = "家常番茄牛腩",
                description = "酸甜汤汁配米饭，适合周末慢慢炖。",
                authorName = "厨房小白也会",
                authorAvatar = testAvatarUrls[10],
                imageUrl = testImageUrls[4],
                likeCount = "6.2万",
                commentCount = "2400",
                collectCount = "1.8万",
                shareCount = "1200",
                tags = listOf("美食", "家常菜"),
                recommendWords = listOf("下饭菜", "炖菜"),
                imageUrls = listOf(testImageUrls[4], testImageUrls[1], testImageUrls[0])
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_study_08",
                title = "番茄钟学习陪伴",
                description = "25 分钟专注和 5 分钟休息，适合晚间复习。",
                authorName = "专注计划",
                authorAvatar = testAvatarUrls[11],
                videoUrl = testVideoUrls[6],
                coverUrl = testImageUrls[5],
                durationText = "01:04",
                likeCount = "11.1万",
                commentCount = "3600",
                collectCount = "2.1万",
                shareCount = "1900",
                tags = listOf("学习", "专注"),
                recommendWords = listOf("番茄钟", "自习陪伴", "复习计划")
            )
        ),
        FeedItem.ImageText(
            ImageTextItem(
                id = "image_movie_05",
                title = "电影海报配色拆解",
                description = "对比冷暖色如何塑造悬疑和浪漫氛围。",
                authorName = "设计放映室",
                authorAvatar = testAvatarUrls[12],
                imageUrl = testImageUrls[0],
                likeCount = "1.9万",
                commentCount = "420",
                collectCount = "3100",
                shareCount = "350",
                tags = listOf("电影", "设计"),
                recommendWords = listOf("海报设计", "色彩"),
                imageUrls = listOf(testImageUrls[0], testImageUrls[5], testImageUrls[2])
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_photo_09",
                title = "手机摄影构图入门",
                description = "用三分线、前景和留白拍出更干净的画面。",
                authorName = "随手拍老师",
                authorAvatar = testAvatarUrls[13],
                videoUrl = testVideoUrls[8],
                coverUrl = testImageUrls[1],
                durationText = "00:53",
                likeCount = "4.9万",
                commentCount = "870",
                collectCount = "9600",
                shareCount = "730",
                tags = listOf("摄影", "手机拍照"),
                recommendWords = listOf("构图", "人像摄影", "拍照教程")
            )
        ),
        FeedItem.ImageText(
            ImageTextItem(
                id = "image_news_06",
                title = "新闻早班车",
                description = "一分钟了解今天值得关注的公共议题。",
                authorName = "快讯编辑部",
                authorAvatar = testAvatarUrls[14],
                imageUrl = testImageUrls[2],
                likeCount = "8.8万",
                commentCount = "4100",
                collectCount = "5200",
                shareCount = "2100",
                tags = listOf("新闻", "资讯"),
                recommendWords = listOf("今日要闻", "民生"),
                imageUrls = listOf(testImageUrls[2], testImageUrls[3], testImageUrls[1])
            )
        ),
        FeedItem.Video(
            VideoItem(
                id = "video_news_10",
                title = "社区新闻现场",
                description = "记者走访老街更新项目，看看城市服务的新变化。",
                authorName = "现场连线",
                authorAvatar = testAvatarUrls[15],
                videoUrl = testVideoUrls[9],
                coverUrl = testImageUrls[3],
                durationText = "12:14",
                likeCount = "10.2万",
                commentCount = "3300",
                collectCount = "6900",
                shareCount = "1700",
                tags = listOf("新闻", "社区"),
                recommendWords = listOf("现场报道", "公共服务")
            )
        )
    )
        .map { it.withExpandedRecommendWords() }
        .map { it.withQualityUrls() }

    private val videoItems: List<VideoItem>
        get() = feedItems.mapNotNull { (it as? FeedItem.Video)?.item }

    fun loadFeedPage(page: Int, pageSize: Int): List<FeedItem> {
        if (page <= 0 || pageSize <= 0) return emptyList()
        val items = if (useRealData) realFeedItems else feedItems
        val fromIndex = (page - 1) * pageSize
        if (fromIndex >= items.size) return emptyList()
        return items.drop(fromIndex).take(pageSize)
    }

    fun searchVideos(keyword: String): List<VideoItem> {
        val videos = if (useRealData) realVideoItems else videoItems
        return SearchRanker()
            .searchVideos(videos, keyword)
            .map { it.video }
    }

    fun loadAllVideos(): List<VideoItem> {
        return if (useRealData) realVideoItems else videoItems
    }

    fun getRecommendWords(itemId: String): List<String> {
        val items = if (useRealData) realFeedItems else feedItems
        val item = items.firstOrNull { it.itemId == itemId } ?: return hotWords
        return RecommendWordEngine(hotWords).buildRecommendWords(item).map { it.word }
    }

    fun findVideoIndexById(videoId: String): Int {
        val videos = if (useRealData) realVideoItems else videoItems
        return videos.indexOfFirst { it.id == videoId }
    }

    private fun MutableList<String>.addUnique(words: List<String>) {
        words.forEach { word ->
            if (word.isNotBlank() && word !in this) {
                add(word)
            }
        }
    }

    private fun FeedItem.withExpandedRecommendWords(): FeedItem {
        val words = aiRecommendWordsById[itemId].orEmpty()
        return when (this) {
            is FeedItem.Video -> copy(item = item.copy(recommendWords = mergeRecommendWords(item.recommendWords, words)))
            is FeedItem.ImageText -> copy(item = item.copy(recommendWords = mergeRecommendWords(item.recommendWords, words)))
        }
    }

    private fun FeedItem.withQualityUrls(): FeedItem {
        if (this !is FeedItem.Video) return this
        val qualityUrls = qualityUrlsById[item.id] ?: return this
        return copy(item = item.copy(qualityUrls = qualityUrls))
    }

    private fun mergeRecommendWords(
        currentWords: List<String>,
        generatedWords: List<String>,
    ): List<String> {
        return (currentWords + generatedWords)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(8)
    }

    private val FeedItem.itemId: String
        get() = when (this) {
            is FeedItem.Video -> item.id
            is FeedItem.ImageText -> item.id
        }

    private val FeedItem.tags: List<String>
        get() = when (this) {
            is FeedItem.Video -> item.tags
            is FeedItem.ImageText -> item.tags
        }

    private val FeedItem.recommendWords: List<String>
        get() = when (this) {
            is FeedItem.Video -> item.recommendWords
            is FeedItem.ImageText -> item.recommendWords
        }

    private companion object {
        const val RECOMMEND_WORD_COUNT = 5

        val aiRecommendWordsById = mapOf(
            "video_travel_01" to listOf("海边旅行", "落日沙滩", "周末旅行", "城市漫步", "旅行攻略", "海岸线"),
            "video_food_02" to listOf("夜市美食", "街头小吃", "本地探店", "小吃攻略", "城市烟火", "深夜食堂"),
            "image_photo_01" to listOf("街头摄影", "雨天拍照", "光影构图", "城市街拍", "摄影技巧", "电影感照片"),
            "video_tech_03" to listOf("折叠屏体验", "手机评测", "数码科技", "效率工具", "多任务体验", "科技新品"),
            "image_study_02" to listOf("学习方法", "笔记整理", "知识管理", "自习效率", "复盘技巧", "高效学习"),
            "video_movie_04" to listOf("电影解析", "经典镜头", "镜头语言", "电影配乐", "光影叙事", "影评推荐"),
            "video_music_05" to listOf("街头音乐", "即兴演奏", "城市声音", "下班路上", "音乐现场", "治愈旋律"),
            "image_city_03" to listOf("城市通勤", "街道观察", "城市生活", "早高峰", "生活记录", "烟火气"),
            "video_city_06" to listOf("城市日落", "天台风景", "晚霞拍摄", "城市漫步", "楼顶视角", "治愈风景"),
            "video_sports_07" to listOf("晨跑热身", "运动健身", "跑前拉伸", "运动计划", "健康生活", "核心训练"),
            "image_food_04" to listOf("家常美食", "番茄牛腩", "下饭菜", "周末炖菜", "厨房教程", "家常菜谱"),
            "video_study_08" to listOf("番茄钟", "自习陪伴", "专注学习", "复习计划", "学习效率", "晚间自习"),
            "image_movie_05" to listOf("海报设计", "电影配色", "视觉设计", "色彩灵感", "电影美学", "设计拆解"),
            "video_photo_09" to listOf("手机摄影", "构图入门", "人像摄影", "拍照教程", "摄影技巧", "留白构图"),
            "image_news_06" to listOf("新闻早班车", "今日要闻", "公共议题", "民生资讯", "热点新闻", "一分钟新闻"),
            "video_news_10" to listOf("社区新闻", "现场报道", "公共服务", "城市更新", "民生现场", "老街变化"),
        )

        val qualityUrlsById = mapOf(
            "video_travel_01" to qualities(
                low = "https://media.w3.org/2010/05/sintel/trailer.mp4",
                medium = "https://media.w3.org/2010/05/bunny/trailer.mp4",
                high = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
            ),
            "video_food_02" to qualities(
                low = "https://www.w3schools.com/html/mov_bbb.mp4",
                medium = "https://vjs.zencdn.net/v/oceans.mp4",
                high = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            ),
            "video_tech_03" to qualities(
                low = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                medium = "https://storage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                high = "https://storage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            ),
            "video_movie_04" to qualities(
                low = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                medium = "https://media.w3.org/2010/05/sintel/trailer.mp4",
                high = "https://media.w3.org/2010/05/bunny/trailer.mp4",
            ),
            "video_music_05" to qualities(
                low = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
                medium = "https://www.w3schools.com/html/mov_bbb.mp4",
                high = "https://vjs.zencdn.net/v/oceans.mp4",
            ),
        )

        fun qualities(
            low: String,
            medium: String,
            high: String,
        ): List<VideoQuality> = listOf(
            VideoQuality(label = "360P", url = low),
            VideoQuality(label = "720P", url = medium),
            VideoQuality(label = "1080P", url = high),
        )
    }
}
