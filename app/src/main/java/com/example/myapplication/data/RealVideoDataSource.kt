package com.example.myapplication.data

import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.ImageTextItem
import com.example.myapplication.model.VideoItem

/**
 * 真实素材数据源
 *
 * 视频来自:
 *   - Pexels (pexels.com) — 免费素材视频, 需 API Key 或直接用 CDN 链接
 *   - Coverr (coverr.co) — 免费素材视频, 无需署名
 *   - Mixkit (mixkit.co) — 免费素材视频
 *
 * 图片来自:
 *   - Unsplash (unsplash.com) — 免费高清图片
 *
 * 头像来自:
 *   - UI-Avatars (ui-avatars.com) — 根据名字生成头像
 *
 * 用法:
 *   在 FakeFeedRepository 同目录下, 通过 AppConfig.useRealData = true 切换
 */
object RealVideoDataSource {

    /**
     * 真实视频列表 — 使用公开 CDN 上的免费素材视频
     * 每个视频匹配中文本地化元数据
     */
    val videos: List<VideoItem> = listOf(
        // ── Pexels 免费视频 (无需 API Key, CDN 直链) ──
        VideoItem(
            id = "real_nature_01",
            title = "山间溪流与森林",
            description = "清澈的山泉流过青苔覆盖的岩石，阳光穿透树叶洒在水面上。",
            authorName = "自然记录者",
            authorAvatar = "https://ui-avatars.com/api/?name=自然记录者&background=D81E06&color=fff&size=150",
            videoUrl = "https://videos.pexels.com/video-files/3194277/3194277-hd_1920_1080_25fps.mp4",
            coverUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=720&h=1280&fit=crop",
            durationText = "00:28",
            likeCount = "8.2万",
            commentCount = "2100",
            collectCount = "1.5万",
            shareCount = "1800",
            tags = listOf("自然", "风景"),
            recommendWords = listOf("森林浴", "山间徒步", "自然白噪音")
        ),
        VideoItem(
            id = "real_city_02",
            title = "上海外滩夜景延时",
            description = "夜幕降临，陆家嘴摩天楼的灯光倒映在黄浦江上。",
            authorName = "城市光影",
            authorAvatar = "https://ui-avatars.com/api/?name=城市光影&background=E13B3C&color=fff&size=150",
            videoUrl = "https://videos.pexels.com/video-files/3129957/3129957-hd_1920_1080_30fps.mp4",
            coverUrl = "https://images.unsplash.com/photo-1537531383496-f4749b88b535?w=720&h=1280&fit=crop",
            durationText = "00:20",
            likeCount = "15.6万",
            commentCount = "5400",
            collectCount = "3.2万",
            shareCount = "4200",
            tags = listOf("城市", "夜景"),
            recommendWords = listOf("外滩", "魔都夜景", "城市风光", "延时摄影")
        ),
        VideoItem(
            id = "real_food_03",
            title = "手工咖啡拉花制作",
            description = "咖啡师用细腻的奶泡在浓缩咖啡上画出精美的叶片图案。",
            authorName = "咖啡日常",
            authorAvatar = "https://ui-avatars.com/api/?name=咖啡日常&background=6B4F4F&color=fff&size=150",
            videoUrl = "https://videos.pexels.com/video-files/4790449/4790449-hd_1920_1080_24fps.mp4",
            coverUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=720&h=1280&fit=crop",
            durationText = "00:15",
            likeCount = "6.8万",
            commentCount = "1800",
            collectCount = "2.1万",
            shareCount = "960",
            tags = listOf("美食", "咖啡"),
            recommendWords = listOf("拉花教程", "手冲咖啡", "咖啡文化")
        ),

        // ── Coverr 免费视频 ──
        VideoItem(
            id = "real_tech_04",
            title = "程序员深夜写代码",
            description = "键盘敲击声和显示器的蓝光，记录一个普通的编程之夜。",
            authorName = "码农日记",
            authorAvatar = "https://ui-avatars.com/api/?name=码农日记&background=2196F3&color=fff&size=150",
            videoUrl = "https://cdn.coverr.co/videos/coverr-typing-on-computer-1584/1080p.mp4",
            coverUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=720&h=1280&fit=crop",
            durationText = "00:23",
            likeCount = "4.5万",
            commentCount = "1200",
            collectCount = "8900",
            shareCount = "670",
            tags = listOf("科技", "编程"),
            recommendWords = listOf("程序员日常", "深夜编程", "效率工具")
        ),
        VideoItem(
            id = "real_sports_05",
            title = "晨跑第一缕阳光",
            description = "清晨6点的城市公园，脚步声和鸟鸣交织。",
            authorName = "跑步者说",
            authorAvatar = "https://ui-avatars.com/api/?name=跑步者说&background=4CAF50&color=fff&size=150",
            videoUrl = "https://cdn.coverr.co/videos/coverr-jogging-in-the-park-5757/1080p.mp4",
            coverUrl = "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=720&h=1280&fit=crop",
            durationText = "00:18",
            likeCount = "5.2万",
            commentCount = "890",
            collectCount = "1.1万",
            shareCount = "450",
            tags = listOf("运动", "晨跑"),
            recommendWords = listOf("晨跑打卡", "公园跑步", "健康生活")
        ),
        VideoItem(
            id = "real_travel_06",
            title = "海浪拍打礁石",
            description = "大西洋的海风呼啸，白色的浪花不断冲击着古老的礁石海岸。",
            authorName = "旅途摄影",
            authorAvatar = "https://ui-avatars.com/api/?name=旅途摄影&background=FF9800&color=fff&size=150",
            videoUrl = "https://cdn.coverr.co/videos/coverr-waves-crashing-on-rocks-3456/1080p.mp4",
            coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=720&h=1280&fit=crop",
            durationText = "00:25",
            likeCount = "12.3万",
            commentCount = "3600",
            collectCount = "2.8万",
            shareCount = "2100",
            tags = listOf("旅行", "海洋"),
            recommendWords = listOf("海边度假", "海浪声", "自然风光")
        ),

        // ── 更多 Pexels 视频 ──
        VideoItem(
            id = "real_music_07",
            title = "街头艺人吉他弹唱",
            description = "繁忙的商业街上，一位歌手用吉他唱着自己写的歌。",
            authorName = "街声日记",
            authorAvatar = "https://ui-avatars.com/api/?name=街声日记&background=9C27B0&color=fff&size=150",
            videoUrl = "https://videos.pexels.com/video-files/3192868/3192868-hd_1920_1080_25fps.mp4",
            coverUrl = "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=720&h=1280&fit=crop",
            durationText = "00:22",
            likeCount = "7.1万",
            commentCount = "1900",
            collectCount = "9600",
            shareCount = "1200",
            tags = listOf("音乐", "街头"),
            recommendWords = listOf("街头表演", "民谣吉他", "原创音乐")
        ),
        VideoItem(
            id = "real_pet_08",
            title = "猫咪的慵懒午后",
            description = "橘猫趴在窗台上，阳光洒在它柔软的毛发上。",
            authorName = "喵星人日常",
            authorAvatar = "https://ui-avatars.com/api/?name=喵星人日常&background=FF5722&color=fff&size=150",
            videoUrl = "https://videos.pexels.com/video-files/4842506/4842506-hd_1920_1080_24fps.mp4",
            coverUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=720&h=1280&fit=crop",
            durationText = "00:17",
            likeCount = "23.4万",
            commentCount = "8700",
            collectCount = "5.6万",
            shareCount = "3400",
            tags = listOf("宠物", "猫咪"),
            recommendWords = listOf("治愈系", "铲屎官", "萌宠日常")
        ),

        // ── Mixkit 替代 ──
        VideoItem(
            id = "real_art_09",
            title = "水墨画创作全过程",
            description = "毛笔蘸墨，在宣纸上勾勒出山水的轮廓。一笔一画都是功夫。",
            authorName = "墨韵轩",
            authorAvatar = "https://ui-avatars.com/api/?name=墨韵轩&background=607D8B&color=fff&size=150",
            videoUrl = "https://videos.pexels.com/video-files/4702603/4702603-hd_1920_1080_24fps.mp4",
            coverUrl = "https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?w=720&h=1280&fit=crop",
            durationText = "00:26",
            likeCount = "9.8万",
            commentCount = "3200",
            collectCount = "1.8万",
            shareCount = "1500",
            tags = listOf("艺术", "国画"),
            recommendWords = listOf("传统文化", "水墨画教程", "书法艺术")
        ),
        VideoItem(
            id = "real_dance_10",
            title = "古典舞水袖表演",
            description = "水袖飞舞，轻盈的身姿配合传统乐器演奏。",
            authorName = "舞者日志",
            authorAvatar = "https://ui-avatars.com/api/?name=舞者日志&background=E91E63&color=fff&size=150",
            videoUrl = "https://videos.pexels.com/video-files/4830227/4830227-hd_1920_1080_24fps.mp4",
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=720&h=1280&fit=crop",
            durationText = "00:19",
            likeCount = "11.2万",
            commentCount = "4100",
            collectCount = "2.6万",
            shareCount = "2800",
            tags = listOf("舞蹈", "传统"),
            recommendWords = listOf("古典舞", "水袖", "国风舞蹈")
        ),
    )

    /**
     * 真实图文数据 — 使用 Unsplash 高质量图片
     */
    val imageTexts: List<ImageTextItem> = listOf(
        ImageTextItem(
            id = "real_image_01",
            title = "故宫初雪",
            description = "红墙白雪，六百年的紫禁城披上了银装。",
            authorName = "故宮摄影",
            authorAvatar = "https://ui-avatars.com/api/?name=故宮摄影&background=D81E06&color=fff&size=150",
            imageUrl = "https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?w=720&h=1280",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?w=720&h=1280",
                "https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=720&h=1280",
                "https://images.unsplash.com/photo-1580651315530-69c8e0026377?w=720&h=1280",
            ),
            likeCount = "18.5万",
            commentCount = "6200",
            collectCount = "4.1万",
            shareCount = "3500",
            tags = listOf("故宫", "雪景"),
            recommendWords = listOf("故宫雪景", "北京旅游", "传统文化")
        ),
        ImageTextItem(
            id = "real_image_02",
            title = "成都火锅图鉴",
            description = "九宫格老火锅，红油翻滚，毛肚七上八下。",
            authorName = "川味探店",
            authorAvatar = "https://ui-avatars.com/api/?name=川味探店&background=FF5722&color=fff&size=150",
            imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=720&h=1280",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=720&h=1280",
                "https://images.unsplash.com/photo-1555126634-323283e090fa?w=720&h=1280",
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=720&h=1280",
            ),
            likeCount = "9.6万",
            commentCount = "3400",
            collectCount = "1.9万",
            shareCount = "1800",
            tags = listOf("美食", "火锅"),
            recommendWords = listOf("重庆火锅", "成都美食", "川菜")
        ),
        ImageTextItem(
            id = "real_image_03",
            title = "西湖落日剪影",
            description = "雷峰塔的轮廓映在金色的湖面上，一叶扁舟划过。",
            authorName = "江南映像",
            authorAvatar = "https://ui-avatars.com/api/?name=江南映像&background=2196F3&color=fff&size=150",
            imageUrl = "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=720&h=1280",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=720&h=1280",
                "https://images.unsplash.com/photo-1533929736458-ca588d08c8be?w=720&h=1280",
                "https://images.unsplash.com/photo-1500530855694-b586d753c34b?w=720&h=1280",
            ),
            likeCount = "14.2万",
            commentCount = "4800",
            collectCount = "3.3万",
            shareCount = "2600",
            tags = listOf("西湖", "落日"),
            recommendWords = listOf("杭州旅游", "西湖日落", "江南风景")
        ),
    )

    /** 将真实视频转为 FeedItem 列表 */
    fun toFeedItems(videoCount: Int = 10, imageCount: Int = 3): List<FeedItem> {
        val items = mutableListOf<FeedItem>()
        videos.take(videoCount).forEach { items.add(FeedItem.Video(it)) }
        imageTexts.take(imageCount).forEach { items.add(FeedItem.ImageText(it)) }
        return items
    }
}
