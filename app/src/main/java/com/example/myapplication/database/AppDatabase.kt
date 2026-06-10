package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.database.dao.FeedDao
import com.example.myapplication.database.dao.PlaybackMetricsDao
import com.example.myapplication.database.dao.SearchHistoryDao
import com.example.myapplication.database.entity.FeedEntity
import com.example.myapplication.database.entity.PlaybackMetricsEntity
import com.example.myapplication.database.entity.SearchHistoryEntity

@Database(
    entities = [
        SearchHistoryEntity::class,
        FeedEntity::class,
        PlaybackMetricsEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun feedDao(): FeedDao
    abstract fun playbackMetricsDao(): PlaybackMetricsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS feed_cache (
                        id TEXT NOT NULL PRIMARY KEY,
                        itemType TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        authorName TEXT NOT NULL,
                        authorAvatar TEXT NOT NULL,
                        videoUrl TEXT,
                        coverUrl TEXT,
                        imageUrl TEXT,
                        durationText TEXT,
                        likeCount TEXT NOT NULL,
                        commentCount TEXT NOT NULL,
                        collectCount TEXT NOT NULL,
                        shareCount TEXT NOT NULL,
                        tagsJson TEXT NOT NULL,
                        recommendWordsJson TEXT NOT NULL,
                        page INTEGER NOT NULL,
                        cachedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val migration2To3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE feed_cache ADD COLUMN qualityUrlsJson TEXT")
            }
        }

        private val migration3To4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS playback_metrics (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        video_id TEXT NOT NULL,
                        video_url TEXT NOT NULL,
                        quality_label TEXT NOT NULL,
                        cold_start_prepare_ms INTEGER NOT NULL,
                        preload_prepare_ms INTEGER NOT NULL,
                        display_start_ms INTEGER NOT NULL,
                        is_preloaded INTEGER NOT NULL DEFAULT 0,
                        improvement_percent INTEGER NOT NULL DEFAULT 0,
                        created_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val migration4To5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 为常用查询添加索引
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_metrics_created ON playback_metrics(created_at)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_metrics_preloaded ON playback_metrics(is_preloaded)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_history_keyword ON search_history(keyword)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "toutiao_video_client.db",
                )
                    .addMigrations(migration1To2, migration2To3, migration3To4, migration4To5)
                    // WAL 模式: 并发读写性能提升
                    .setJournalMode(androidx.room.RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
