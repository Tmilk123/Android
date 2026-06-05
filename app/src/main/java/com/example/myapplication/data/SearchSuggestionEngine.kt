package com.example.myapplication.data

import com.example.myapplication.model.FeedItem
import com.example.myapplication.model.VideoItem

/**
 * 搜索联想/预取词引擎
 *
 * 从所有内容的标题、标签、推荐词、作者名中提取词汇，
 * 按用户输入的前缀匹配，返回排序后的联想建议。
 */
class SearchSuggestionEngine(
    private val fakeRepository: FakeFeedRepository = FakeFeedRepository(),
) {

    /** 所有可被搜索到的词汇 (构建一次后缓存) */
    private val vocabulary: List<ScoredTerm> by lazy { buildVocabulary() }

    /**
     * 根据用户输入的前缀，返回匹配的联想词列表
     * @param prefix 用户已输入的文字
     * @param limit  最多返回条数
     */
    fun suggest(prefix: String, limit: Int = 8): List<ScoredTerm> {
        val query = prefix.trim()
        if (query.isEmpty()) return emptyList()

        return vocabulary
            .filter { term ->
                term.word.contains(query, ignoreCase = true)
            }
            .sortedWith(
                compareByDescending<ScoredTerm> { it.sourcePriority }
                    .thenByDescending { it.score }
                    .thenBy { it.word.length }
            )
            .take(limit)
    }

    /** 获取热门搜索词 (无输入时展示) */
    fun hotSearchWords(limit: Int = 10): List<ScoredTerm> {
        return vocabulary
            .sortedWith(
                compareByDescending<ScoredTerm> { it.score }
                    .thenByDescending { it.sourcePriority }
            )
            .take(limit)
    }

    // ── 词汇表构建 ──

    private fun buildVocabulary(): List<ScoredTerm> {
        val terms = mutableMapOf<String, ScoredTerm>()
        val allItems = fakeRepository.loadFeedPage(1, 50)
        val allVideos = fakeRepository.loadAllVideos()
        val hotWords = listOf("今日头条", "旅行", "美食", "科技", "电影", "城市")
        val engine = RecommendWordEngine(hotWords)

        // 1. 从所有 FeedItem 提取: 标题关键词 + 标签 + 推荐词
        allItems.forEach { item ->
            when (item) {
                is FeedItem.Video -> collectFromVideo(item.item, engine, terms)
                is FeedItem.ImageText -> collectFromImageText(item.item, engine, terms)
            }
        }

        // 2. 全局热词 (最高分)
        hotWords.forEach { word ->
            mergeTerm(terms, word, "全局热词", 200, sourcePriority = 5)
        }

        // 3. 作者名也可被搜索
        allVideos.forEach { video ->
            mergeTerm(terms, video.authorName, "作者", 30, sourcePriority = 1)
        }

        return terms.values.toList()
    }

    private fun collectFromVideo(
        video: VideoItem,
        engine: RecommendWordEngine,
        terms: MutableMap<String, ScoredTerm>,
    ) {
        // 标题拆词
        video.title.split(" ", "，", "。", "、", "-", "_", "的", "在", "和")
            .map { it.trim() }
            .filter { it.length in 2..10 }
            .forEach { word ->
                mergeTerm(terms, word, "标题", 150, sourcePriority = 4)
            }

        // 标签 (完整标签作为一个词)
        video.tags.forEach { tag ->
            mergeTerm(terms, tag, "标签", 120, sourcePriority = 3)
        }

        // 推荐词
        video.recommendWords.forEach { word ->
            mergeTerm(terms, word, "推荐词", 100, sourcePriority = 3)
        }

        // 全文标题作为联想词 (长词)
        mergeTerm(terms, video.title, "内容标题", 90, sourcePriority = 2)
    }

    private fun collectFromImageText(
        item: com.example.myapplication.model.ImageTextItem,
        engine: RecommendWordEngine,
        terms: MutableMap<String, ScoredTerm>,
    ) {
        item.title.split(" ", "，", "。", "、", "-", "_", "的", "在", "和")
            .map { it.trim() }
            .filter { it.length in 2..10 }
            .forEach { word ->
                mergeTerm(terms, word, "标题", 100, sourcePriority = 3)
            }
        item.tags.forEach { tag ->
            mergeTerm(terms, tag, "标签", 80, sourcePriority = 2)
        }
        item.recommendWords.forEach { word ->
            mergeTerm(terms, word, "推荐词", 70, sourcePriority = 2)
        }
    }

    private fun mergeTerm(
        map: MutableMap<String, ScoredTerm>,
        word: String,
        source: String,
        score: Int,
        sourcePriority: Int,
    ) {
        val key = word.trim()
        if (key.length < 2 || key.length > 30) return

        val existing = map[key]
        map[key] = if (existing == null || score > existing.score) {
            ScoredTerm(word = key, source = source, score = score, sourcePriority = sourcePriority)
        } else {
            existing
        }
    }

    data class ScoredTerm(
        val word: String,
        val source: String,
        val score: Int,
        val sourcePriority: Int,
    )
}
