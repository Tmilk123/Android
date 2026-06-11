package com.example.myapplication.data

import com.example.myapplication.database.dao.SearchHistoryDao
import com.example.myapplication.database.entity.SearchHistoryEntity
import com.example.myapplication.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SearchHistoryDataSource {
    fun getAllHistory(): Flow<List<SearchHistoryItem>>
    suspend fun insertHistory(keyword: String)
    suspend fun deleteHistoryById(id: Long)
    suspend fun clearHistory()
}

class SearchHistoryRepository(
    private val dao: SearchHistoryDao,
) : SearchHistoryDataSource {

    override fun getAllHistory(): Flow<List<SearchHistoryItem>> {
        return dao.getAllHistory().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun insertHistory(keyword: String) {
        val trimmedKeyword = keyword.trim()
        if (trimmedKeyword.isEmpty()) return

        try {
            dao.insertHistory(
                SearchHistoryEntity(
                    keyword = trimmedKeyword,
                    createdAt = System.currentTimeMillis(),
                )
            )
            android.util.Log.d("SearchHistory", "Saved: $trimmedKeyword")
        } catch (e: Exception) {
            android.util.Log.e("SearchHistory", "Failed to save: ${e.message}", e)
        }
    }

    override suspend fun deleteHistoryById(id: Long) {
        dao.deleteHistoryById(id)
    }

    override suspend fun clearHistory() {
        dao.clearHistory()
    }

    private fun SearchHistoryEntity.toModel(): SearchHistoryItem {
        return SearchHistoryItem(
            id = id,
            keyword = keyword,
            createdAt = createdAt,
        )
    }
}
