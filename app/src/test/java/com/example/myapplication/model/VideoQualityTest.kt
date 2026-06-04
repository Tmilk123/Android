package com.example.myapplication.model

import org.junit.Assert.assertEquals
import org.junit.Test

class VideoQualityTest {

    @Test
    fun selectedQualityPrefers720pWhenAvailable() {
        val item = video(
            qualityUrls = listOf(
                VideoQuality(label = "360P", url = "url_360"),
                VideoQuality(label = "720P", url = "url_720"),
                VideoQuality(label = "1080P", url = "url_1080"),
            )
        )

        assertEquals("720P", item.defaultQuality().label)
        assertEquals("url_720", item.defaultPlaybackUrl())
    }

    @Test
    fun selectedQualityFallsBackToFirstQualityWhen720pIsMissing() {
        val item = video(
            qualityUrls = listOf(
                VideoQuality(label = "480P", url = "url_480"),
                VideoQuality(label = "1080P", url = "url_1080"),
            )
        )

        assertEquals("480P", item.defaultQuality().label)
        assertEquals("url_480", item.defaultPlaybackUrl())
    }

    @Test
    fun selectedQualityFallsBackToVideoUrlWhenQualityUrlsAreEmpty() {
        val item = video(videoUrl = "default_url")

        assertEquals("默认", item.defaultQuality().label)
        assertEquals("default_url", item.defaultPlaybackUrl())
    }

    @Test
    fun findQualityIgnoresLabelCase() {
        val item = video(
            qualityUrls = listOf(VideoQuality(label = "1080P", url = "url_1080"))
        )

        assertEquals("url_1080", item.findQuality("1080p")?.url)
    }

    private fun video(
        videoUrl: String = "fallback_url",
        qualityUrls: List<VideoQuality> = emptyList(),
    ): VideoItem = VideoItem(
        id = "video_id",
        title = "title",
        description = "description",
        authorName = "author",
        authorAvatar = "",
        videoUrl = videoUrl,
        coverUrl = "",
        durationText = "00:10",
        likeCount = "0",
        commentCount = "0",
        collectCount = "0",
        shareCount = "0",
        tags = emptyList(),
        recommendWords = emptyList(),
        qualityUrls = qualityUrls,
    )
}
