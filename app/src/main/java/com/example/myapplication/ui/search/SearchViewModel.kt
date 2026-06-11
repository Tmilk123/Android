package com.example.myapplication.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.SearchHistoryDataSource
import com.example.myapplication.model.SearchHistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchHistoryDataSource,
) : ViewModel() {

    private val _history = MutableStateFlow<List<SearchHistoryItem>>(emptyList())
    val history: StateFlow<List<SearchHistoryItem>> = _history.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllHistory().collect { items ->
                _history.value = items
            }
        }
    }

    fun submitSearch(input: String): String? {
        val keyword = input.trim()
        if (keyword.isEmpty()) return null

        // 必须用 runBlocking 确保写入完成再跳转, 否则 ViewModel 销毁会取消写入
        kotlinx.coroutines.runBlocking {
            repository.insertHistory(keyword)
        }
        return keyword
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryById(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
