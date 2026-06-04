# 性能优化报告 — 今日头条视频播放流

> **日期**: 2026-06-04  
> **数据标注**: ✅ 已验证 = 可复现的实测数据 | ⏳ 估算 = 基于架构的合理推算 | 📱 待测 = 需在设备上实际运行采集

---

## 1. 视频起播性能优化 📱

### 1.1 预加载机制

通过 `PlayerManager.preloadNext()` 在当前视频播放时提前创建独立 ExoPlayer 实例并 `prepare()` 下一个视频。用户滑动后调用 `promotePreloadPlayer()` 直接切换：

```
冷启动路径:  setMediaItem() → prepare() → [网络请求] → [解码器初始化] → STATE_READY
预加载路径:  promotePreloadPlayer() → [切换 listener] → 立即可播放
```

### 1.2 指标采集架构 ✅

每次起播都会通过 `PlayerManager.recordFirstReady()` 记录：

```kotlin
// PlayerManager.kt:285-306
val metrics = PlaybackMetrics(
    videoId = videoId,
    coldStartPrepareMs = if (isPreloaded) coldBaselineMs else readyCostMs,
    preloadPrepareMs = if (isPreloaded) readyCostMs else 0L,
    isPreloaded = currentWasPreloaded,
    improvementPercent = ...
)
// → logMetrics() → metricsRepository.recordMetrics() → Room DB
```

数据存入 `playback_metrics` 表，通过 `PlaybackMetricsDao.getAggregateStats()` 聚合查询：

```sql
SELECT
    COUNT(*) as totalCount,
    AVG(cold_start_prepare_ms) as avgColdStartMs,
    AVG(preload_prepare_ms) as avgPreloadMs,
    AVG(improvement_percent) as avgImprovementPct,
    CAST(SUM(CASE WHEN is_preloaded=1 THEN 1 ELSE 0 END) AS REAL)/COUNT(*)*100 as preloadHitRate
FROM playback_metrics
```

### 1.3 如何获取真实数据 📱

```
1. 在 Android Studio 中 Run 到模拟器/真机
2. 上下滑动视频流，播放 10+ 个视频（覆盖冷启动和预加载两种路径）
3. 点击右上角「起播指标」→「📊 更多指标」查看聚合统计
4. 记录 avgColdStartMs / avgPreloadMs / avgImprovementPct / preloadHitRate
```

### 1.4 预期效果 ⏳

| 场景 | 冷启动起播 (预估) | 预加载起播 (预估) | 优化原理 |
|------|------------------|------------------|---------|
| CDN/WiFi 环境 | 200-800ms | <20ms | 跳过 DNS + TCP + prepare() |
| 弱网环境 | 1500-5000ms | <20ms | 播放器已在内存中就绪，与网络无关 |

预加载命中时优化幅度应 **>95%**（因为完全跳过了网络请求和播放器初始化）。

---

## 2. 代码体积优化 ✅

### 2.1 模板清理（已执行）

| 指标 | 值 | 验证方式 |
|------|-----|---------|
| 删除文件 | 11 个 | `git log --diff-filter=D` |
| 当前 app 模块 Kotlin 文件 | 41 个 | `find app/src/main/java -name "*.kt" \| wc -l` ✅ 已验证 |
| viewBinding | 已关闭 | `app/build.gradle.kts` 中 `viewBinding = true` 已移除 ✅ |

删除清单：
- `FirstFragment.kt`, `SecondFragment.kt` — 模板 Fragment，从未被实例化
- `activity_main.xml`, `content_main.xml`, `fragment_first.xml`, `fragment_second.xml` — 模板布局，MainActivity 使用 Compose
- `nav_graph.xml`, `menu_main.xml` — 模板导航/菜单
- `FeedScreenPlaceholder.kt`, `SearchScreenPlaceholder.kt`, `SearchResultScreenPlaceholder.kt` — 占位 Composable，从未被调用

---

## 3. 跨平台代码复用 ✅

### 3.1 KMP 共享模块

```
shared/src/commonMain/kotlin/com/example/myapplication/shared/
├── model/
│   ├── VideoItem.kt          (含 defaultQuality/defaultPlaybackUrl/findQuality)
│   ├── ImageTextItem.kt
│   ├── FeedItem.kt           (sealed class: Video | ImageText)
│   ├── RecommendWord.kt      (含 SOURCE_* 常量)
│   └── SearchRankedVideo.kt
└── search/
    ├── SearchRanker.kt        (加权评分算法: 标题100/标签70/推荐词60/作者40/描述30)
    └── RecommendWordEngine.kt (4源融合: AI生成+标签+标题关键词+热词)

shared/src/androidMain/  ← Android target
shared/src/iosMain/      ← iOS target (x64, arm64, simulatorArm64)
```

### 3.2 已验证指标 ✅

| 指标 | 值 | 验证命令 |
|------|-----|---------|
| KMP 文件数 | 7 | `find shared/src -name "*.kt" \| wc -l` |
| 共享代码行数 | 323 | `cat shared/src/**/*.kt \| wc -l` |
| 支持平台 | Android + iOS (3 targets) | `shared/build.gradle.kts` |
| Android 集成 | 薄封装 delegate 模式 | `app/.../data/SearchRanker.kt` → `sharedRanker.searchVideos()` |

---

## 4. 搜索算法 ✅

### 4.1 排名评分（来自源码 `SearchRanker.kt`）

| 匹配维度 | 权重 | 匹配方式 |
|---------|------|---------|
| 标题精确匹配 | 100 | `title.contains(keyword, ignoreCase=true)` |
| 标题部分匹配 | 80 | 2-字符滑动窗口 + 分词 token 匹配 |
| 标签匹配 | 70 | `tags.filter { it.contains(keyword) }` |
| 推荐词匹配 | 60 | `recommendWords.filter { it.contains(keyword) }` |
| 作者名匹配 | 40 | `authorName.contains(keyword)` |
| 描述匹配 | 30 | `description.contains(keyword)` |

算法复杂度: O(n) 线性扫描，当前数据集 10 条视频，结果即时返回。

---

## 5. 网络韧性 ✅

### 5.1 自动重试策略（来自源码 `PlayerManager.kt`）

```kotlin
const val MAX_RETRIES = 3
const val RETRY_DELAY_BASE_MS = 500L  // 递增: 500ms → 1000ms → 1500ms

override fun onPlayerError(error: PlaybackException) {
    retryCount++
    if (retryCount < maxRetries) scheduleRetry()  // 自动重试
}
```

### 5.2 手动重试 UI（来自源码 `VideoFeedCard.kt`）

错误状态显示「视频加载失败」+「重试 (N/3)」+「点击重试」按钮，调用 `playerManager.retry()`。

---

## 6. 需求完成度 ✅

| 类别 | 完成 | 总计 | 验证方式 |
|------|------|------|---------|
| 视频功能 | 8 | 8 | 源码逐项核对 |
| 搜索功能 | 6 | 6 | 源码逐项核对 |
| 进阶要求 | 5 | 5 | 源码逐项核对 |
| 补充要求 | 4 | 4 | 含 KMP 跨平台模块 |
| **总计** | **23** | **23** | |
