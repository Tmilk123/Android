package com.example.myapplication.shared.search

import com.example.myapplication.shared.model.SearchRankedVideo
import com.example.myapplication.shared.model.VideoItem

class SearchRanker {

    fun searchVideos(
        videos: List<VideoItem>,
        keyword: String,
    ): List<SearchRankedVideo> {
        val query = keyword.trim()
        if (query.isEmpty()) return emptyList()

        return videos
            .mapNotNull { video ->
                val result = scoreVideo(video, query)
                if (result.score <= 0) null else SearchRankedVideo(
                    video = video,
                    score = result.score,
                    matchedWords = result.matchedWords,
                )
            }
            .sortedWith(
                compareByDescending<SearchRankedVideo> { it.score }
                    .thenBy { it.video.id }
            )
    }

    private fun scoreVideo(video: VideoItem, keyword: String): ScoreResult {
        var score = 0
        val matchedWords = mutableListOf<String>()

        if (video.title.contains(keyword, ignoreCase = true)) {
            score += 100
        } else if (video.title.partiallyMatches(keyword)) {
            score += 80
        }

        val matchedTags = video.tags.filter { it.contains(keyword, ignoreCase = true) }
        if (matchedTags.isNotEmpty()) {
            score += 70
            matchedWords.addUnique(matchedTags)
        }

        val matchedRecommendWords = video.recommendWords.filter {
            it.contains(keyword, ignoreCase = true)
        }
        if (matchedRecommendWords.isNotEmpty()) {
            score += 60
            matchedWords.addUnique(matchedRecommendWords)
        }

        if (video.authorName.contains(keyword, ignoreCase = true)) {
            score += 40
            matchedWords.addUnique(listOf(video.authorName))
        }

        if (video.description.contains(keyword, ignoreCase = true)) {
            score += 30
        }

        return ScoreResult(
            score = score,
            matchedWords = matchedWords,
        )
    }

    private fun MutableList<String>.addUnique(words: List<String>) {
        words.forEach { word ->
            val trimmedWord = word.trim()
            if (trimmedWord.isNotEmpty() && none { it.equals(trimmedWord, ignoreCase = true) }) {
                add(trimmedWord)
            }
        }
    }

    private fun String.partiallyMatches(keyword: String): Boolean {
        val title = lowercase()
        val query = keyword.lowercase()
        val keywordParts = query
            .split(Regex("\\s+"))
            .filter { it.length >= PARTIAL_MATCH_MIN_LENGTH }

        if (keywordParts.any { title.contains(it) }) return true

        return query
            .windowed(size = PARTIAL_MATCH_MIN_LENGTH, step = 1, partialWindows = false)
            .any { title.contains(it) }
    }

    private data class ScoreResult(
        val score: Int,
        val matchedWords: List<String>,
    )

    companion object {
        private const val PARTIAL_MATCH_MIN_LENGTH = 2
    }
}
