package com.example.myapplication.data

import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.VideoItem
import com.example.myapplication.model.RecommendWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendWordEngineTest {

    @Test
    fun buildRecommendWordsPrioritizesAiGeneratedWords() {
        val item = FeedItem.Video(
            VideoItem(
                id = "video_test",
                title = "城市日落旅行",
                description = "记录天台晚霞和城市风景",
                authorName = "tester",
                authorAvatar = "",
                videoUrl = "",
                coverUrl = "",
                durationText = "01:00",
                likeCount = "1",
                commentCount = "1",
                collectCount = "1",
                shareCount = "1",
                tags = listOf("城市", "旅行"),
                recommendWords = listOf("天台晚霞", "城市日落", "旅行攻略", "风景拍摄"),
            )
        )

        val words = RecommendWordEngine().buildRecommendWords(item)

        assertEquals(5, words.size)
        assertEquals(RecommendWord.SOURCE_AI_GENERATED, words.first().source)
        assertTrue(words.any { it.word == "城市日落" })
    }
}
