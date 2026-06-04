package com.example.myapplication.shared.model

data class RecommendWord(
    val word: String,
    val source: String,
    val score: Int,
    val reason: String,
) {
    companion object {
        const val SOURCE_AI_GENERATED = "ai_generated"
        const val SOURCE_TAG_BASED = "tag_based"
        const val SOURCE_HOT_WORD = "hot_word"
        const val SOURCE_MANUAL = "manual"
    }
}
