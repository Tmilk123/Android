package com.example.myapplication.ui.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FeedRepository
import com.example.myapplication.model.FeedItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FeedUiState(
    val items: List<FeedItem> = emptyList(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = true,
)

class FeedViewModel(
    val repository: FeedRepository,
) : ViewModel() {

    var uiState by mutableStateOf(FeedUiState(isLoading = true))
        private set

    private var currentPage = 0
    private var currentCategory: String = "推荐"
    private var loadJob: Job? = null

    init {
        loadInitialData()
    }

    /** 切换分类 → 重新加载 */
    fun selectCategory(category: String) {
        if (category == currentCategory) return
        currentCategory = category
        currentPage = 0
        uiState = FeedUiState(isLoading = true)
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            loadNextPage()
        }
    }

    fun loadNextPageIfNeeded(currentIndex: Int) {
        val shouldLoad = currentIndex >= uiState.items.size - LOAD_MORE_THRESHOLD
        if (shouldLoad) {
            viewModelScope.launch { loadNextPage() }
        }
    }

    suspend fun loadUntilVideo(videoId: String): Int {
        // 先在已加载项中查找
        findLoadedVideoIndex(videoId).takeIf { it >= 0 }?.let { return it }

        while (uiState.hasMore) {
            loadNextPage()
            findLoadedVideoIndex(videoId).takeIf { it >= 0 }?.let { return it }
        }
        return -1
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val cached = repository.loadCachedFeed()
            if (cached.items.isNotEmpty()) {
                currentPage = cached.lastPage
                uiState = FeedUiState(
                    items = cached.items,
                    isLoading = false,
                    hasMore = true,
                )
            } else {
                uiState = FeedUiState(isLoading = false)
                loadNextPage()
            }
        }
    }

    private suspend fun loadNextPage() {
        if (!uiState.hasMore) return

        uiState = uiState.copy(isLoading = true)
        val nextPage = currentPage + 1
        val newItems = repository.loadFeedPage(page = nextPage, pageSize = PAGE_SIZE)

        currentPage = if (newItems.isEmpty()) currentPage else nextPage
        uiState = uiState.copy(
            items = uiState.items + newItems,
            isLoading = false,
            hasMore = newItems.size == PAGE_SIZE,
        )
    }

    private fun findLoadedVideoIndex(videoId: String): Int {
        return uiState.items.indexOfFirst { item ->
            item is FeedItem.Video && item.item.id == videoId
        }
    }

    companion object {
        const val PAGE_SIZE = 5
        const val LOAD_MORE_THRESHOLD = 2
    }
}
