package com.example.myapplication.shared.model

sealed class FeedItem {
    abstract val title: String
    abstract val description: String
    abstract val tags: List<String>
    abstract val recommendWords: List<String>

    data class Video(val item: VideoItem) : FeedItem() {
        override val title get() = item.title
        override val description get() = item.description
        override val tags get() = item.tags
        override val recommendWords get() = item.recommendWords
    }

    data class ImageText(val item: ImageTextItem) : FeedItem() {
        override val title get() = item.title
        override val description get() = item.description
        override val tags get() = item.tags
        override val recommendWords get() = item.recommendWords
    }
}
