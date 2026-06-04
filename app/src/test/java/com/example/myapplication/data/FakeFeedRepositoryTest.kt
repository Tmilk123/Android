package com.example.myapplication.data

import com.example.myapplication.model.FeedItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeFeedRepositoryTest {

    private val repository = FakeFeedRepository()

    @Test
    fun loadFeedPageReturnsPagedItems() {
        val firstPage = repository.loadFeedPage(page = 1, pageSize = 5)
        val secondPage = repository.loadFeedPage(page = 2, pageSize = 5)
        val emptyPage = repository.loadFeedPage(page = 5, pageSize = 5)

        assertEquals(5, firstPage.size)
        assertEquals(5, secondPage.size)
        assertEquals(emptyList<FeedItem>(), emptyPage)
        assertEquals("video_travel_01", (firstPage.first() as FeedItem.Video).item.id)
    }

    @Test
    fun repositoryContainsTenVideosAndSixImageTextItems() {
        val allItems = repository.loadFeedPage(page = 1, pageSize = 20)

        assertEquals(16, allItems.size)
        assertEquals(10, allItems.count { it is FeedItem.Video })
        assertEquals(6, allItems.count { it is FeedItem.ImageText })
    }

    @Test
    fun atLeastFiveVideosContainMultipleQualityUrls() {
        val videos = repository.loadAllVideos()
        val videosWithQualities = videos.filter { it.qualityUrls.size >= 3 }

        assertTrue(videosWithQualities.size >= 5)
        videosWithQualities.take(5).forEach { video ->
            assertEquals(listOf("360P", "720P", "1080P"), video.qualityUrls.map { it.label })
        }
    }

    @Test
    fun everyItemHasAtLeastFourRecommendWords() {
        val allItems = repository.loadFeedPage(page = 1, pageSize = 20)

        allItems.forEach { item ->
            val words = when (item) {
                is FeedItem.Video -> item.item.recommendWords
                is FeedItem.ImageText -> item.item.recommendWords
            }
            assertTrue(words.size >= 4)
            assertTrue(words.size <= 8)
        }
    }

    @Test
    fun getRecommendWordsReturnsTopFiveWords() {
        val words = repository.getRecommendWords("video_sports_07")

        assertEquals(5, words.size)
        assertTrue(words.isNotEmpty())
    }

    @Test
    fun findVideoIndexByIdReturnsIndexInVideoListOnly() {
        assertEquals(0, repository.findVideoIndexById("video_travel_01"))
        assertEquals(9, repository.findVideoIndexById("video_news_10"))
        assertEquals(-1, repository.findVideoIndexById("image_city_03"))
        assertEquals(-1, repository.findVideoIndexById("missing"))
    }
}
