package com.example.myapplication.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.database.entity.FeedEntity

@Dao
interface FeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItems(items: List<FeedEntity>)

    @Query("SELECT * FROM feed_cache ORDER BY page ASC, cachedAt ASC")
    suspend fun getAllCachedFeed(): List<FeedEntity>

    @Query("SELECT COALESCE(MAX(page), 0) FROM feed_cache")
    suspend fun getMaxCachedPage(): Int
}
