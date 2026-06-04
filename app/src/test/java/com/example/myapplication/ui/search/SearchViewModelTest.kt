package com.example.myapplication.ui.search

import com.example.myapplication.data.SearchHistoryDataSource
import com.example.myapplication.model.SearchHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun submitSearchSavesTrimmedKeywordToHistory() = runTest {
        val repository = FakeSearchHistoryDataSource()
        val viewModel = SearchViewModel(repository)

        val keyword = viewModel.submitSearch("  city  ")
        advanceUntilIdle()

        assertEquals("city", keyword)
        assertEquals(listOf("city"), viewModel.history.value.map { it.keyword })
    }

    @Test
    fun submitSearchMovesExistingKeywordToTop() = runTest {
        val repository = FakeSearchHistoryDataSource()
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch("travel")
        viewModel.submitSearch("city")
        viewModel.submitSearch("travel")
        advanceUntilIdle()

        assertEquals(listOf("travel", "city"), viewModel.history.value.map { it.keyword })
    }

    @Test
    fun deleteAndClearHistoryUpdateHistoryList() = runTest {
        val repository = FakeSearchHistoryDataSource()
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch("travel")
        viewModel.submitSearch("food")
        advanceUntilIdle()
        val firstId = viewModel.history.value.first().id

        viewModel.deleteHistory(firstId)
        advanceUntilIdle()

        assertEquals(listOf("travel"), viewModel.history.value.map { it.keyword })

        viewModel.clearHistory()
        advanceUntilIdle()

        assertEquals(emptyList<String>(), viewModel.history.value.map { it.keyword })
    }
}

private class FakeSearchHistoryDataSource : SearchHistoryDataSource {
    private val items = MutableStateFlow<List<SearchHistoryItem>>(emptyList())
    private var nextId = 1L

    override fun getAllHistory(): Flow<List<SearchHistoryItem>> = items

    override suspend fun insertHistory(keyword: String) {
        val trimmedKeyword = keyword.trim()
        val item = SearchHistoryItem(
            id = nextId++,
            keyword = trimmedKeyword,
            createdAt = System.currentTimeMillis(),
        )
        items.value = listOf(item) + items.value.filterNot { it.keyword == trimmedKeyword }
    }

    override suspend fun deleteHistoryById(id: Long) {
        items.value = items.value.filterNot { it.id == id }
    }

    override suspend fun clearHistory() {
        items.value = emptyList()
    }
}
