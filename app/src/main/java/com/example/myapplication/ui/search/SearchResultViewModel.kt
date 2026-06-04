package com.example.myapplication.ui.search

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.FakeFeedRepository
import com.example.myapplication.data.SearchRankedVideo
import com.example.myapplication.data.SearchRanker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SearchResultUiState(
    val keyword: String = "",
    val results: List<SearchRankedVideo> = emptyList(),
)

class SearchResultViewModel(
    private val repository: FakeFeedRepository = FakeFeedRepository(),
    private val searchRanker: SearchRanker = SearchRanker(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchResultUiState())
    val uiState: StateFlow<SearchResultUiState> = _uiState.asStateFlow()

    fun search(keyword: String) {
        val query = keyword.trim()
        _uiState.value = SearchResultUiState(
            keyword = query,
            results = if (query.isEmpty()) {
                emptyList()
            } else {
                searchRanker.searchVideos(repository.loadAllVideos(), query)
            },
        )
    }
}
