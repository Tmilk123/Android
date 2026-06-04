package com.example.myapplication.data

import com.example.myapplication.model.VideoItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchRankerTest {

    private val ranker = SearchRanker()

    @Test
    fun searchTrimsKeywordAndIgnoresCase() {
        val results = ranker.searchVideos(
            videos = listOf(
                video(id = "ai_title", title = "AI Camera Test"),
                video(id = "no_match", title = "Travel Notes"),
            ),
            keyword = "  ai  ",
        )

        assertEquals(listOf("ai_title"), results.map { it.video.id })
        assertEquals(100, results.first().score)
    }

    @Test
    fun searchAccumulatesScoresAndSortsDescending() {
        val results = ranker.searchVideos(
            videos = listOf(
                video(
                    id = "tag_and_recommend",
                    title = "City vlog",
                    tags = listOf("travel"),
                    recommendWords = listOf("travel guide"),
                ),
                video(
                    id = "title_only",
                    title = "Travel story",
                ),
            ),
            keyword = "travel",
        )

        assertEquals(listOf("tag_and_recommend", "title_only"), results.map { it.video.id })
        assertEquals(130, results.first().score)
        assertEquals(listOf("travel", "travel guide"), results.first().matchedWords)
    }

    @Test
    fun searchReturnsOnlyPositiveScoreResults() {
        val results = ranker.searchVideos(
            videos = listOf(
                video(id = "description_match", description = "A quiet food market"),
                video(id = "no_match", title = "Morning run"),
            ),
            keyword = "food",
        )

        assertEquals(listOf("description_match"), results.map { it.video.id })
        assertEquals(30, results.first().score)
    }

    @Test
    fun tagsRankHigherThanRecommendWordsWhenOtherScoresAreEqual() {
        val results = ranker.searchVideos(
            videos = listOf(
                video(id = "recommend_match", recommendWords = listOf("movie")),
                video(id = "tag_match", tags = listOf("movie")),
            ),
            keyword = "movie",
        )

        assertEquals(listOf("tag_match", "recommend_match"), results.map { it.video.id })
        assertTrue(results[0].score > results[1].score)
    }

    private fun video(
        id: String,
        title: String = "Untitled",
        description: String = "",
        authorName: String = "Author",
        tags: List<String> = emptyList(),
        recommendWords: List<String> = emptyList(),
    ): VideoItem = VideoItem(
        id = id,
        title = title,
        description = description,
        authorName = authorName,
        authorAvatar = "",
        videoUrl = "",
        coverUrl = "",
        durationText = "00:10",
        likeCount = "0",
        commentCount = "0",
        collectCount = "0",
        shareCount = "0",
        tags = tags,
        recommendWords = recommendWords,
    )
}
