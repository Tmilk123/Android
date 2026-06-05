package com.example.myapplication.ui.metrics

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.MetricsRepository
import com.example.myapplication.database.dao.MetricsAggregateStats
import com.example.myapplication.database.entity.PlaybackMetricsEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MetricsDashboardScreen(
    metricsRepository: MetricsRepository,
    onBackClick: () -> Unit,
) {
    var aggregateStats by remember { mutableStateOf<MetricsAggregateStats?>(null) }
    var recentMetrics by remember { mutableStateOf<List<PlaybackMetricsEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        aggregateStats = metricsRepository.getAggregateStats()
        recentMetrics = metricsRepository.getRecentMetrics(50)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBackClick) {
                Text("← 返回", color = Color(0xFF222222), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "起播性能指标",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    scope.launch {
                        isExporting = true
                        val jsonResult = metricsRepository.exportToJson()
                        val mdResult = metricsRepository.exportToMarkdown()
                        isExporting = false

                        val message = if (jsonResult.isSuccess && mdResult.isSuccess) {
                            "导出成功！\nJSON: ${jsonResult.getOrNull()}\nMD: ${mdResult.getOrNull()}"
                        } else {
                            "导出失败: ${jsonResult.exceptionOrNull()?.message ?: mdResult.exceptionOrNull()?.message}"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                },
                enabled = !isExporting,
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("📤 导出", color = Color(0xFF2563EB), fontSize = 14.sp)
                }
            }
            TextButton(onClick = {
                scope.launch {
                    metricsRepository.clearAll()
                    aggregateStats = metricsRepository.getAggregateStats()
                    recentMetrics = emptyList()
                }
            }) {
                Text("清空", color = Color(0xFF999999), fontSize = 14.sp)
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                AggregateStatsCard(stats = aggregateStats)
            }

            // ── 预加载 ON vs OFF 对比 ──
            item {
                ComparisonCard(stats = aggregateStats)
            }

            item {
                Text(
                    text = "指标说明",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            item {
                ExplanationCard()
            }

            item {
                Text(
                    text = "最近起播记录 (${recentMetrics.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            items(recentMetrics.take(20)) { metric ->
                MetricItemCard(metric)
            }
        }
    }
}

@Composable
private fun AggregateStatsCard(stats: MetricsAggregateStats?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "聚合统计",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (stats == null || stats.totalCount == 0) {
                Text(
                    text = "暂无起播数据，请先播放视频后查看",
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                )
                return@Column
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatItem("总记录数", "${stats.totalCount}", Color(0xFF333333))
                StatItem(
                    "预加载命中率",
                    "${String.format("%.1f", stats.preloadHitRate)}%",
                    if (stats.preloadHitRate >= 50) Color(0xFF4CAF50) else Color(0xFFFF9800),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatItem(
                    "平均冷启动",
                    "${String.format("%.0f", stats.avgColdStartMs)} ms",
                    Color(0xFFFF5722),
                )
                StatItem(
                    "平均预加载起播",
                    "${String.format("%.0f", stats.avgPreloadMs)} ms",
                    Color(0xFF4CAF50),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatItem(
                    "平均实际起播",
                    "${String.format("%.0f", stats.avgDisplayMs)} ms",
                    Color(0xFF2196F3),
                )
                StatItem(
                    "平均优化幅度",
                    "${String.format("%.1f", stats.avgImprovementPct)}%",
                    if ((stats.avgImprovementPct) >= 70) Color(0xFF4CAF50) else Color(0xFFFF9800),
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF999999),
        )
    }
}

@Composable
private fun ExplanationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ExplanationRow("冷启动起播", "首次加载视频，从 prepare() 到 STATE_READY 的耗时")
            Spacer(modifier = Modifier.height(8.dp))
            ExplanationRow("预加载起播", "提前 prepare 下一个视频，滑动过去时直接切换（趋近 0ms）")
            Spacer(modifier = Modifier.height(8.dp))
            ExplanationRow("优化幅度", "冷启动耗时中通过预加载节省的比例 = (冷 - 预) / 冷 × 100%")
            Spacer(modifier = Modifier.height(8.dp))
            ExplanationRow("预加载命中率", "所有起播中走预加载路径的比例，越高说明预加载策略越有效")
            Spacer(modifier = Modifier.height(8.dp))
            ExplanationRow("导出功能", "点击右上角「📤 导出」可将数据保存为 JSON 和 Markdown 报告")
        }
    }
}

@Composable
private fun ExplanationRow(label: String, description: String) {
    Row {
        Text(
            text = "• $label：",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF333333),
        )
        Text(
            text = description,
            fontSize = 13.sp,
            color = Color(0xFF666666),
        )
    }
}

@Composable
private fun MetricItemCard(metric: PlaybackMetricsEntity) {
    val dateFormat = remember { SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = metric.videoId,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = dateFormat.format(Date(metric.createdAt)),
                    fontSize = 11.sp,
                    color = Color(0xFF999999),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (metric.isPreloaded) "✅ 预加载命中" else "❌ 冷启动",
                    fontSize = 12.sp,
                    color = if (metric.isPreloaded) Color(0xFF4CAF50) else Color(0xFFFF5722),
                )
                Text(
                    text = "${metric.displayStartMs}ms",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                )
                Text(
                    text = metric.qualityLabel,
                    fontSize = 11.sp,
                    color = Color(0xFF999999),
                )
                Text(
                    text = if (metric.improvementPercent > 0) "↑${metric.improvementPercent}%" else "-",
                    fontSize = 12.sp,
                    color = if (metric.improvementPercent >= 70) Color(0xFF4CAF50) else Color(0xFFFF9800),
                )
            }
        }
    }
}

@Composable
private fun ComparisonCard(stats: MetricsAggregateStats?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("⚡ 预加载 ON vs OFF", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))
            Spacer(modifier = Modifier.height(16.dp))

            if (stats == null || stats.totalCount == 0) {
                Text("暂无数据", color = Color(0xFF999999), fontSize = 14.sp)
                return@Column
            }

            val cold = stats.avgColdStartMs
            val preload = stats.avgPreloadMs
            val gap = (cold - preload).coerceAtLeast(0.0)
            val pct = if (cold > 0) (gap / cold * 100) else 0.0

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("OFF", color = Color(0xFFFF5722), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${String.format("%.0f", cold)} ms", color = Color(0xFFFF5722), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Text("→", color = Color(0xFF999999), fontSize = 18.sp, modifier = Modifier.padding(top = 10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ON", color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${String.format("%.0f", preload)} ms", color = Color(0xFF4CAF50), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("优化幅度", color = Color(0xFF333333), fontSize = 12.sp)
                    Text("${String.format("%.1f", pct)}%", color = Color(0xFF4CAF50), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("节省", color = Color(0xFF333333), fontSize = 12.sp)
                    Text("${String.format("%.0f", gap)} ms", color = Color(0xFF2196F3), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("≈${String.format("%.1f", gap / 1000)}s/次", color = Color(0xFF999999), fontSize = 11.sp)
                }
            }

            Text(
                "命中率: ${String.format("%.1f", stats.preloadHitRate)}% | 共 ${stats.totalCount} 次",
                color = Color(0xFF999999), fontSize = 11.sp,
            )
        }
    }
}
