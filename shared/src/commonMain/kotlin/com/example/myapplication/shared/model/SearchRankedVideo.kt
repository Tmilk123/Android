package com.example.myapplication.shared.model

data class SearchRankedVideo(
    val video: VideoItem,
    val score: Int,
    val matchedWords: List<String>,
)
