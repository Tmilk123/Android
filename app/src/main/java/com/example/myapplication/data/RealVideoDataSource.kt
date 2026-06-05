package com.example.myapplication.data

import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.ImageTextItem
import com.example.myapplication.model.VideoItem

/**
 * 已验证的真实视频素材数据源
 *
 * 所有 URL 均通过 HTTP 200 验证 (2026-06-05)
 *
 * 视频来源:
 *   - W3.org 官方测试视频 (Sintel, Bunny)
 *   - MDN Mozilla (Flower)
 *   - W3Schools (Big Buck Bunny)
 *   - VideoJS CDN (Oceans)
 *   - dl6.webmfiles.org (Big Buck Bunny trailer webm)
 *   - filesamples.com, freetestdata.com, samplelib.com (通用样片)
 *
 * 如需真实 Pexels 视频: 注册免费 API Key → https://www.pexels.com/api/
 *
 * 图片来源: Unsplash (unsplash.com) — 免费商用, 无需 API Key
 * 头像来源: UI-Avatars (ui-avatars.com) — 根据名字生成
 */
object RealVideoDataSource {

    val videos: List<VideoItem> = listOf(
        VideoItem(
            id = "real_sintel_01",
            title = "寻龙记 — 动画短片精选",
            description = "小女孩在雪地中发现受伤的龙宝宝，展开一段奇幻冒险。",
            authorName = "动画放映厅",
            authorAvatar = "https://ui-avatars.com/api/?name=动画放映厅&background=D81E06&color=fff&size=150",
            videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4",
            coverUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=720&h=1280&fit=crop",
            durationText = "00:52",
            likeCount = "13.5万",
            commentCount = "4200",
            collectCount = "2.8万",
            shareCount = "3100",
            tags = listOf("动画", "短片"),
            recommendWords = listOf("动画电影", "CG短片", "奇幻冒险")
        ),
        VideoItem(
            id = "real_bunny_02",
            title = "兔八哥搞笑片段合集",
            description = "机灵的小兔子用各种巧妙办法躲避猎人的追捕。",
            authorName = "爆笑短片",
            authorAvatar = "https://ui-avatars.com/api/?name=爆笑短片&background=FF9800&color=fff&size=150",
            videoUrl = "https://media.w3.org/2010/05/bunny/trailer.mp4",
            coverUrl = "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=720&h=1280&fit=crop",
            durationText = "01:02",
            likeCount = "22.1万",
            commentCount = "8900",
            collectCount = "5.4万",
            shareCount = "7600",
            tags = listOf("搞笑", "动画"),
            recommendWords = listOf("搞笑视频", "兔八哥", "减压神器")
        ),
        VideoItem(
            id = "real_flower_03",
            title = "花朵绽放 — 绝美延时摄影",
            description = "一株兰花的完整开放过程，每一帧都是壁纸级画面。",
            authorName = "自然之美",
            authorAvatar = "https://ui-avatars.com/api/?name=自然之美&background=4CAF50&color=fff&size=150",
            videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
            coverUrl = "https://images.unsplash.com/photo-1490750967868-88aa4f44baee?w=720&h=1280&fit=crop",
            durationText = "00:31",
            likeCount = "8.9万",
            commentCount = "2100",
            collectCount = "3.2万",
            shareCount = "1400",
            tags = listOf("自然", "延时"),
            recommendWords = listOf("延时摄影", "花开", "治愈系")
        ),
        VideoItem(
            id = "real_bbb_04",
            title = "大雄兔搞笑剧场",
            description = "贪吃的大兔子偷吃果园果实，引发一连串爆笑事件。",
            authorName = "搞笑笑园",
            authorAvatar = "https://ui-avatars.com/api/?name=搞笑笑园&background=E91E63&color=fff&size=150",
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            coverUrl = "https://images.unsplash.com/photo-1452570053594-1b985d6ea890?w=720&h=1280&fit=crop",
            durationText = "00:10",
            likeCount = "19.3万",
            commentCount = "5600",
            collectCount = "4.1万",
            shareCount = "5200",
            tags = listOf("搞笑", "动物"),
            recommendWords = listOf("搞笑短片", "萌宠", "减压")
        ),
        VideoItem(
            id = "real_ocean_05",
            title = "深邃海洋 — 4K 高清纪录片片段",
            description = "从珊瑚礁到深海沟，海面之下藏着另一个世界。",
            authorName = "蓝色星球",
            authorAvatar = "https://ui-avatars.com/api/?name=蓝色星球&background=2196F3&color=fff&size=150",
            videoUrl = "https://vjs.zencdn.net/v/oceans.mp4",
            coverUrl = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=720&h=1280&fit=crop",
            durationText = "00:46",
            likeCount = "16.7万",
            commentCount = "4800",
            collectCount = "6.3万",
            shareCount = "3800",
            tags = listOf("海洋", "纪录片"),
            recommendWords = listOf("海洋世界", "纪录片推荐", "4K视频")
        ),
        VideoItem(
            id = "real_city_06",
            title = "城市街头 — 慢镜头实拍",
            description = "雨后的街道倒映着霓虹灯，行人匆匆而过。",
            authorName = "街头摄影师",
            authorAvatar = "https://ui-avatars.com/api/?name=街头摄影师&background=607D8B&color=fff&size=150",
            videoUrl = "https://woolyss.com/f/spring-video-camera.mp4",
            coverUrl = "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=720&h=1280&fit=crop",
            durationText = "00:21",
            likeCount = "11.2万",
            commentCount = "3200",
            collectCount = "2.1万",
            shareCount = "1900",
            tags = listOf("城市", "街拍"),
            recommendWords = listOf("城市风光", "街拍", "慢镜头")
        ),
        VideoItem(
            id = "real_tech_07",
            title = "数字世界的几何之美",
            description = "动态图形展示数学与代码交汇处的视觉盛宴。",
            authorName = "数字艺术馆",
            authorAvatar = "https://ui-avatars.com/api/?name=数字艺术馆&background=9C27B0&color=fff&size=150",
            videoUrl = "https://samplelib.com/lib/preview/mp4/sample-5s.mp4",
            coverUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=720&h=1280&fit=crop",
            durationText = "00:05",
            likeCount = "5.6万",
            commentCount = "1400",
            collectCount = "1.8万",
            shareCount = "870",
            tags = listOf("科技", "艺术"),
            recommendWords = listOf("数字艺术", "视效", "创意")
        ),
        VideoItem(
            id = "real_sports_08",
            title = "足球精彩过人集锦",
            description = "绿茵场上的灵动脚步，一次次令人惊叹的盘带突破。",
            authorName = "球场风云",
            authorAvatar = "https://ui-avatars.com/api/?name=球场风云&background=795548&color=fff&size=150",
            videoUrl = "https://filesamples.com/samples/video/mp4/sample_640x360.mp4",
            coverUrl = "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=720&h=1280&fit=crop",
            durationText = "00:15",
            likeCount = "14.8万",
            commentCount = "6200",
            collectCount = "3.5万",
            shareCount = "4100",
            tags = listOf("运动", "足球"),
            recommendWords = listOf("足球集锦", "过人技巧", "精彩进球")
        ),
        VideoItem(
            id = "real_food_09",
            title = "手工面包烘焙教程",
            description = "从揉面到出炉，外酥内软的欧式面包在家也能做。",
            authorName = "烘焙教室",
            authorAvatar = "https://ui-avatars.com/api/?name=烘焙教室&background=FF5722&color=fff&size=150",
            videoUrl = "https://freetestdata.com/wp-content/uploads/2022/02/Free_Test_Data_1MB_MP4.mp4",
            coverUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=720&h=1280&fit=crop",
            durationText = "00:08",
            likeCount = "7.3万",
            commentCount = "2800",
            collectCount = "2.4万",
            shareCount = "1100",
            tags = listOf("美食", "烘焙"),
            recommendWords = listOf("烘焙教程", "面包", "手工美食")
        ),
        VideoItem(
            id = "real_travel_10",
            title = "桂林山水甲天下",
            description = "漓江竹筏漂流，看喀斯特峰林在晨雾中若隐若现。",
            authorName = "在路上旅拍",
            authorAvatar = "https://ui-avatars.com/api/?name=在路上旅拍&background=009688&color=fff&size=150",
            videoUrl = "https://dl6.webmfiles.org/big-buck-bunny_trailer.webm",
            coverUrl = "https://images.unsplash.com/photo-1528127269322-539801943592?w=720&h=1280&fit=crop",
            durationText = "00:33",
            likeCount = "20.5万",
            commentCount = "7400",
            collectCount = "5.8万",
            shareCount = "4900",
            tags = listOf("旅行", "桂林"),
            recommendWords = listOf("桂林旅游", "漓江", "山水风光", "国内游")
        ),
    )

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

    fun toFeedItems(videoCount: Int = 10, imageCount: Int = 3): List<FeedItem> {
        val items = mutableListOf<FeedItem>()
        videos.take(videoCount).forEach { items.add(FeedItem.Video(it)) }
        imageTexts.take(imageCount).forEach { items.add(FeedItem.ImageText(it)) }
        return items
    }
}
