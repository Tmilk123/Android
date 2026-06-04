package com.example.myapplication.model

sealed class FeedItem {
    data class Video(val item: VideoItem) : FeedItem()
    data class ImageText(val item: ImageTextItem) : FeedItem()
}
