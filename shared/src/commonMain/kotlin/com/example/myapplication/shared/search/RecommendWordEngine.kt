package com.example.myapplication.shared.search

import com.example.myapplication.shared.model.FeedItem
import com.example.myapplication.shared.model.RecommendWord

class RecommendWordEngine(
    private val hotWords: List<String> = defaultHotWords,
) {

    fun buildRecommendWords(item: FeedItem, limit: Int = DEFAULT_LIMIT): List<RecommendWord> {
        val title = item.title
        val description = item.description
        val tags = item.tags

        val candidates = buildList {
            item.recommendWords.forEach { word ->
                add(
                    RecommendWord(
                        word = word,
                        source = RecommendWord.SOURCE_AI_GENERATED,
                        score = baseScoreFor(word, title, description, tags) + AI_SOURCE_BONUS,
                        reason = "模拟 AI 根据标题、描述和标签生成",
                    )
                )
            }

            tags.forEach { tag ->
                add(
                    RecommendWord(
                        word = tag,
                        source = RecommendWord.SOURCE_TAG_BASED,
                        score = baseScoreFor(tag, title, description, tags),
                        reason = "由内容标签补充",
                    )
                )
            }

            titleKeywords(title).forEach { keyword ->
                add(
                    RecommendWord(
                        word = keyword,
                        source = RecommendWord.SOURCE_MANUAL,
                        score = baseScoreFor(keyword, title, description, tags),
                        reason = "由标题关键词生成",
                    )
                )
            }

            hotWords.forEach { hotWord ->
                add(
                    RecommendWord(
                        word = hotWord,
                        source = RecommendWord.SOURCE_HOT_WORD,
                        score = baseScoreFor(hotWord, title, description, tags) + HOT_WORD_SCORE,
                        reason = "全局热门搜索词兜底",
                    )
                )
            }
        }

        return candidates
            .filter { it.word.isNotBlank() }
            .groupBy { it.word }
            .map { (_, sameWords) ->
                sameWords.maxWith(
                    compareBy<RecommendWord> { it.score }
                        .thenBy { if (it.source == RecommendWord.SOURCE_AI_GENERATED) 1 else 0 }
                )
            }
            .sortedWith(
                compareByDescending<RecommendWord> { it.source == RecommendWord.SOURCE_AI_GENERATED }
                    .thenByDescending { it.score }
                    .thenBy { it.word.length }
            )
            .take(limit.coerceIn(MIN_LIMIT, MAX_LIMIT))
    }

    private fun baseScoreFor(
        word: String,
        title: String,
        description: String,
        tags: List<String>,
    ): Int {
        var score = 0
        if (title.contains(word, ignoreCase = true) || word.containsAnyTokenOf(title)) {
            score += TITLE_MATCH_SCORE
        }
        if (tags.any { tag ->
            tag.contains(word, ignoreCase = true) || word.contains(tag, ignoreCase = true)
        }) {
            score += TAG_MATCH_SCORE
        }
        if (description.contains(word, ignoreCase = true) || word.containsAnyTokenOf(description)) {
            score += DESCRIPTION_MATCH_SCORE
        }
        return score
    }

    private fun titleKeywords(title: String): List<String> {
        return title
            .split(" ", "，", "。", "、", "-", "_")
            .map { it.trim() }
            .filter { it.length in 2..8 }
            .take(3)
    }

    private fun String.containsAnyTokenOf(text: String): Boolean {
        return text
            .split(" ", "，", "。", "、", "-", "_")
            .map { it.trim() }
            .filter { it.length >= 2 }
            .any { token -> contains(token, ignoreCase = true) }
    }

    companion object {
        private const val TITLE_MATCH_SCORE = 40
        private const val TAG_MATCH_SCORE = 30
        private const val DESCRIPTION_MATCH_SCORE = 20
        private const val HOT_WORD_SCORE = 10
        private const val AI_SOURCE_BONUS = 100
        private const val MIN_LIMIT = 3
        private const val DEFAULT_LIMIT = 5
        private const val MAX_LIMIT = 5

        val defaultHotWords = listOf("今日头条", "旅行", "美食", "科技", "电影", "城市")
    }
}
