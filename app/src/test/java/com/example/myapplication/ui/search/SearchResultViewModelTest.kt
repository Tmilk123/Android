package com.example.myapplication.ui.search

import com.example.myapplication.data.FakeFeedRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchResultViewModelTest {

    @Test
    fun searchLoadsRankedVideoResultsForKeyword() {
        val viewModel = SearchResultViewModel()
        val keyword = FakeFeedRepository().loadAllVideos().first().tags.first()

        viewModel.search(keyword)

        assertTrue(viewModel.uiState.value.results.isNotEmpty())
        assertEquals(keyword, viewModel.uiState.value.keyword)
        assertTrue(viewModel.uiState.value.results.all { it.score > 0 })
    }

    @Test
    fun searchReturnsEmptyResultsForBlankKeyword() {
        val viewModel = SearchResultViewModel()

        viewModel.search(" ")

        assertEquals(emptyList<String>(), viewModel.uiState.value.results.map { it.video.id })
    }
}
