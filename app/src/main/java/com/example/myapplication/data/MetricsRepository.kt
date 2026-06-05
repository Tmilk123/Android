package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.database.dao.MetricsAggregateStats
import com.example.myapplication.database.dao.PlaybackMetricsDao
import com.example.myapplication.database.entity.PlaybackMetricsEntity
import com.example.myapplication.player.PlaybackMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MetricsRepository(
    private val dao: PlaybackMetricsDao,
    private val context: Context? = null,
) {

    suspend fun recordMetrics(metrics: PlaybackMetrics, qualityLabel: String) {
        withContext(Dispatchers.IO) {
            dao.insertMetrics(
                PlaybackMetricsEntity(
                    videoId = metrics.videoId,
                    videoUrl = metrics.videoUrl,
                    qualityLabel = qualityLabel,
                    coldStartPrepareMs = metrics.coldStartPrepareMs,
                    preloadPrepareMs = metrics.preloadPrepareMs,
                    displayStartMs = metrics.displayStartMs,
                    isPreloaded = metrics.isPreloaded,
                    improvementPercent = metrics.improvementPercent,
                )
            )
        }
    }

    suspend fun getAggregateStats(): MetricsAggregateStats {
        return withContext(Dispatchers.IO) {
            dao.getAggregateStats()
        }
    }

    suspend fun getRecentMetrics(limit: Int = 50): List<PlaybackMetricsEntity> {
        return withContext(Dispatchers.IO) {
            dao.getRecentMetrics(limit)
        }
    }

    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            dao.clearAll()
        }
    }

    suspend fun exportToJson(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val stats = dao.getAggregateStats()
                val metricsList = dao.getRecentMetrics(1000)
                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
                val fileName = "playback_metrics_$timestamp.json"

                val outputDir = context?.getExternalFilesDir(null) ?: context?.filesDir
                if (outputDir == null) {
                    return@withContext Result.failure(IllegalStateException("Cannot access app storage"))
                }

                val outputFile = File(outputDir, fileName)
                FileWriter(outputFile).use { writer ->
                    writer.write("{\n")
                    writer.write("  \"exportTimestamp\": \"${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())}\",\n")
                    writer.write("  \"aggregateStats\": {\n")
                    writer.write("    \"totalCount\": ${stats.totalCount},\n")
                    writer.write("    \"avgColdStartMs\": ${stats.avgColdStartMs},\n")
                    writer.write("    \"avgPreloadMs\": ${stats.avgPreloadMs},\n")
                    writer.write("    \"avgDisplayMs\": ${stats.avgDisplayMs},\n")
                    writer.write("    \"avgImprovementPct\": ${stats.avgImprovementPct},\n")
                    writer.write("    \"preloadHitRate\": ${stats.preloadHitRate}\n")
                    writer.write("  },\n")
                    writer.write("  \"metrics\": [\n")

                    metricsList.forEachIndexed { index, metric ->
                        writer.write("    {\n")
                        writer.write("      \"videoId\": \"${metric.videoId}\",\n")
                        writer.write("      \"qualityLabel\": \"${metric.qualityLabel}\",\n")
                        writer.write("      \"coldStartPrepareMs\": ${metric.coldStartPrepareMs},\n")
                        writer.write("      \"preloadPrepareMs\": ${metric.preloadPrepareMs},\n")
                        writer.write("      \"displayStartMs\": ${metric.displayStartMs},\n")
                        writer.write("      \"isPreloaded\": ${metric.isPreloaded},\n")
                        writer.write("      \"improvementPercent\": ${metric.improvementPercent},\n")
                        writer.write("      \"createdAt\": ${metric.createdAt}\n")
                        writer.write("    }${if (index < metricsList.size - 1) "," else ""}\n")
                    }

                    writer.write("  ]\n")
                    writer.write("}\n")
                }

                Result.success(outputFile.absolutePath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun exportToMarkdown(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val stats = dao.getAggregateStats()
                val metricsList = dao.getRecentMetrics(1000)
                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
                val fileName = "playback_metrics_report_$timestamp.md"

                val outputDir = context?.getExternalFilesDir(null) ?: context?.filesDir
                if (outputDir == null) {
                    return@withContext Result.failure(IllegalStateException("Cannot access app storage"))
                }

                val outputFile = File(outputDir, fileName)
                FileWriter(outputFile).use { writer ->
                    writer.write("# 视频起播性能优化报告\n\n")
                    writer.write("> 导出时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n\n")
                    writer.write("---\n\n")
                    writer.write("## 1. 聚合统计\n\n")
                    writer.write("| 指标 | 值 |\n")
                    writer.write("|------|----|\n")
                    writer.write("| 总记录数 | ${stats.totalCount} |\n")
                    writer.write("| 平均冷启动 | ${String.format("%.0f", stats.avgColdStartMs)} ms |\n")
                    writer.write("| 平均预加载起播 | ${String.format("%.0f", stats.avgPreloadMs)} ms |\n")
                    writer.write("| 平均实际起播 | ${String.format("%.0f", stats.avgDisplayMs)} ms |\n")
                    writer.write("| 平均优化幅度 | ${String.format("%.1f", stats.avgImprovementPct)}% |\n")
                    writer.write("| 预加载命中率 | ${String.format("%.1f", stats.preloadHitRate)}% |\n\n")
                    writer.write("---\n\n")
                    writer.write("## 2. 指标说明\n\n")
                    writer.write("- **冷启动起播**: 首次加载视频，从 prepare() 到 STATE_READY 的耗时\n")
                    writer.write("- **预加载起播**: 提前 prepare 下一个视频，滑动过去时直接切换\n")
                    writer.write("- **优化幅度**: 冷启动耗时中通过预加载节省的比例 = (冷 - 预) / 冷 × 100%\n")
                    writer.write("- **预加载命中率**: 所有起播中走预加载路径的比例\n\n")
                    writer.write("---\n\n")
                    writer.write("## 3. 详细记录\n\n")
                    writer.write("| 序号 | 视频ID | 类型 | 起播耗时(ms) | 优化幅度 | 画质 | 时间 |\n")
                    writer.write("|------|--------|------|-------------|---------|------|------|\n")

                    val dateFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
                    metricsList.reversed().forEachIndexed { index, metric ->
                        val typeLabel = if (metric.isPreloaded) "✅ 预加载" else "❌ 冷启动"
                        val improveLabel = if (metric.improvementPercent > 0) "${metric.improvementPercent}%" else "-"
                        writer.write("| ${index + 1} | ${metric.videoId} | $typeLabel | ${metric.displayStartMs} | $improveLabel | ${metric.qualityLabel} | ${dateFormat.format(Date(metric.createdAt))} |\n")
                    }
                }

                Result.success(outputFile.absolutePath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
