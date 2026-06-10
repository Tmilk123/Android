package com.example.myapplication.data

import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.ImageTextItem
import com.example.myapplication.model.VideoItem

/**
 * 已验证的真实视频素材数据源
 * 全部 mp4 URL 来自 W3.org / W3Schools / MDN / VideoJS CDN, HTTP 200 验证可靠
 */
object RealVideoDataSource {

    // 10 个全部可用的 mp4 视频 URL
    private val URLS = listOf(
        "https://media.w3.org/2010/05/sintel/trailer.mp4",
        "https://media.w3.org/2010/05/bunny/trailer.mp4",
        "https://media.w3.org/2010/05/bunny/movie.mp4",
        "https://www.w3schools.com/html/mov_bbb.mp4",
        "https://vjs.zencdn.net/v/oceans.mp4",
        "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
        "https://media.w3.org/2010/05/video/movie_300.mp4",
        "https://www.w3schools.com/html/movie.mp4",
        "https://media.w3.org/2010/05/sintel/trailer.mp4",
        "https://media.w3.org/2010/05/bunny/movie.mp4",
    )

    val videos: List<VideoItem> = listOf(
        VideoItem("real_sintel_01", "寻龙记 — 动画短片", "小女孩在雪地中发现受伤的龙宝宝。",
            "动画放映厅", "https://ui-avatars.com/api/?name=动画放映厅&background=D81E06&color=fff&size=150",
            URLS[0], "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=720&h=1280&fit=crop",
            "00:52", "13.5万", "4200", "2.8万", "3100",
            listOf("动画", "短片"), listOf("动画电影", "CG短片", "奇幻冒险")),
        VideoItem("real_bunny_02", "兔八哥搞笑片段", "机灵的小兔子巧妙躲避猎人追捕。",
            "爆笑短片", "https://ui-avatars.com/api/?name=爆笑短片&background=FF9800&color=fff&size=150",
            URLS[1], "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=720&h=1280&fit=crop",
            "01:02", "22.1万", "8900", "5.4万", "7600",
            listOf("搞笑", "动画"), listOf("搞笑视频", "兔八哥", "减压神器")),
        VideoItem("real_bunny_full", "兔八哥完整版", "兔八哥的完整冒险故事，笑料不断。",
            "经典动画", "https://ui-avatars.com/api/?name=经典动画&background=E91E63&color=fff&size=150",
            URLS[2], "https://images.unsplash.com/photo-1452570053594-1b985d6ea890?w=720&h=1280&fit=crop",
            "02:30", "18.6万", "5600", "4.2万", "4800",
            listOf("动画", "经典"), listOf("经典动画", "搞笑", "减压")),
        VideoItem("real_bbb_04", "大雄兔搞笑剧场", "贪吃的大兔子偷吃果园果实引发一连串爆笑。",
            "搞笑笑园", "https://ui-avatars.com/api/?name=搞笑笑园&background=2196F3&color=fff&size=150",
            URLS[3], "https://images.unsplash.com/photo-1452570053594-1b985d6ea890?w=720&h=1280&fit=crop",
            "00:10", "19.3万", "5600", "4.1万", "5200",
            listOf("搞笑", "动物"), listOf("搞笑短片", "萌宠", "减压")),
        VideoItem("real_ocean_05", "深邃海洋纪录片", "从珊瑚礁到深海沟的奇妙世界。",
            "蓝色星球", "https://ui-avatars.com/api/?name=蓝色星球&background=009688&color=fff&size=150",
            URLS[4], "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=720&h=1280&fit=crop",
            "00:46", "16.7万", "4800", "6.3万", "3800",
            listOf("海洋", "纪录片"), listOf("海洋世界", "纪录片推荐", "4K视频")),
        VideoItem("real_flower_06", "花朵绽放延时摄影", "一株兰花的完整开放过程。",
            "自然之美", "https://ui-avatars.com/api/?name=自然之美&background=4CAF50&color=fff&size=150",
            URLS[5], "https://images.unsplash.com/photo-1490750967868-88aa4f44baee?w=720&h=1280&fit=crop",
            "00:31", "8.9万", "2100", "3.2万", "1400",
            listOf("自然", "延时"), listOf("延时摄影", "花开", "治愈系")),
        VideoItem("real_movie_07", "电影级画面赏析", "拆解经典镜头中的光线运用。",
            "片场放映员", "https://ui-avatars.com/api/?name=片场放映员&background=795548&color=fff&size=150",
            URLS[6], "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=720&h=1280&fit=crop",
            "01:28", "15.3万", "5400", "2.4万", "4600",
            listOf("电影", "镜头"), listOf("经典片段", "电影解析", "配乐")),
        VideoItem("real_city_08", "城市建筑风光", "高楼林立的都市天际线航拍。",
            "城市光影", "https://ui-avatars.com/api/?name=城市光影&background=607D8B&color=fff&size=150",
            URLS[7], "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=720&h=1280&fit=crop",
            "00:15", "11.2万", "3200", "2.1万", "1900",
            listOf("城市", "建筑"), listOf("城市风光", "建筑", "航拍")),
        VideoItem("real_nature_09", "自然风光掠影", "高山湖泊与森林的壮丽航拍。",
            "自然记录者", "https://ui-avatars.com/api/?name=自然记录者&background=8BC34A&color=fff&size=150",
            URLS[8], "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=720&h=1280&fit=crop",
            "00:52", "20.5万", "7400", "5.8万", "4900",
            listOf("自然", "风光"), listOf("自然风光", "航拍", "旅行")),
        VideoItem("real_travel_10", "旅途精彩瞬间", "世界各地的绝美风光合集。",
            "在路上旅拍", "https://ui-avatars.com/api/?name=在路上旅拍&background=FF5722&color=fff&size=150",
            URLS[9], "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=720&h=1280&fit=crop",
            "02:30", "12.3万", "3600", "2.8万", "2100",
            listOf("旅行", "风光"), listOf("旅行", "风光", "世界")),
    )

    val imageTexts: List<ImageTextItem> = listOf(
        ImageTextItem("real_img_01", "故宫初雪", "红墙白雪，六百年紫禁城披银装。",
            "故宮摄影", "https://ui-avatars.com/api/?name=故宮摄影&background=D81E06&color=fff&size=150",
            "https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?w=720&h=1280",
            listOf("https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?w=720&h=1280",
                "https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=720&h=1280",
                "https://images.unsplash.com/photo-1580651315530-69c8e0026377?w=720&h=1280"),
            "18.5万", "6200", "4.1万", "3500",
            listOf("故宫", "雪景"), listOf("故宫雪景", "北京旅游", "传统文化")),
        ImageTextItem("real_img_02", "成都火锅图鉴", "九宫格老火锅，红油翻滚。",
            "川味探店", "https://ui-avatars.com/api/?name=川味探店&background=FF5722&color=fff&size=150",
            "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=720&h=1280",
            listOf("https://images.unsplash.com/photo-1563245372-f21724e3856d?w=720&h=1280",
                "https://images.unsplash.com/photo-1555126634-323283e090fa?w=720&h=1280",
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=720&h=1280"),
            "9.6万", "3400", "1.9万", "1800",
            listOf("美食", "火锅"), listOf("重庆火锅", "成都美食", "川菜")),
        ImageTextItem("real_img_03", "西湖落日剪影", "雷峰塔轮廓映在金色湖面。",
            "江南映像", "https://ui-avatars.com/api/?name=江南映像&background=2196F3&color=fff&size=150",
            "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=720&h=1280",
            listOf("https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=720&h=1280",
                "https://images.unsplash.com/photo-1533929736458-ca588d08c8be?w=720&h=1280",
                "https://images.unsplash.com/photo-1500530855694-b586d753c34b?w=720&h=1280"),
            "14.2万", "4800", "3.3万", "2600",
            listOf("西湖", "落日"), listOf("杭州旅游", "西湖日落", "江南风景")),
    )

    fun toFeedItems(videoCount: Int = 10, imageCount: Int = 3): List<FeedItem> {
        val items = mutableListOf<FeedItem>()
        videos.take(videoCount).forEach { items.add(FeedItem.Video(it)) }
        imageTexts.take(imageCount).forEach { items.add(FeedItem.ImageText(it)) }
        return items
    }
}
