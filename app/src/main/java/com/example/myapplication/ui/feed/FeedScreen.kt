package com.example.myapplication.ui.feed

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import com.example.myapplication.data.AppConfig
import com.example.myapplication.data.MetricsRepository
import com.example.myapplication.data.FakeFeedRepository
import com.example.myapplication.data.FeedRepository
import com.example.myapplication.data.RecommendWordEngine
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.FeedItem
import com.example.myapplication.player.PlaybackMetrics
import com.example.myapplication.player.PlayerManager

@UnstableApi
@Composable
fun FeedScreen(
    targetId: String? = null,
    metricsRepository: MetricsRepository? = null,
    onSearchClick: () -> Unit,
    onRecommendWordClick: (String) -> Unit,
    onMetricsClick: (() -> Unit)? = null,
    viewModel: FeedViewModel? = null,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val actualViewModel = viewModel ?: remember {
        val database = AppDatabase.getDatabase(context)
        FeedViewModel(
            FeedRepository(
                fakeFeedRepository = FakeFeedRepository(useRealData = AppConfig.dataSource != "fake"),
                feedDao = database.feedDao(),
            )
        )
    }
    val playerManager = remember {
        PlayerManager(
            context = context,
            metricsRepository = metricsRepository,
            onVideoUrlExpired = { videoId ->
                actualViewModel.repository.refreshVideoUrl(videoId)
            },
        )
    }
    val recommendWordEngine = remember { RecommendWordEngine() }
    val uiState = actualViewModel.uiState
    val pagerState = rememberPagerState(pageCount = { uiState.items.size })
    var showDebugMetrics by remember { mutableStateOf(false) }
    var preloadEnabled by remember { mutableStateOf(true) }
    var isLandscapeFullscreen by remember { mutableStateOf(false) }
    var useRealData by remember { mutableStateOf(AppConfig.dataSource != "fake") }
    var selectedTab by remember { mutableIntStateOf(0) }
    playerManager.preloadEnabled = preloadEnabled

    // Fix status bar icons for black background (light icons)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    LaunchedEffect(pagerState.currentPage, uiState.items.size) {
        if (uiState.items.isNotEmpty()) {
            actualViewModel.loadNextPageIfNeeded(pagerState.currentPage)
        }
    }

    LaunchedEffect(targetId) {
        val videoId = targetId?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        val targetPage = actualViewModel.loadUntilVideo(videoId)
        if (targetPage >= 0) {
            pagerState.scrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState.currentPage, uiState.items.size) {
        val currentItem = uiState.items.getOrNull(pagerState.currentPage)
        when (currentItem) {
            is FeedItem.Video -> {
                val nextItem = uiState.items.getOrNull(pagerState.currentPage + 1)
                if (nextItem is FeedItem.Video) {
                    playerManager.preloadNext(nextItem.item)
                } else {
                    playerManager.clearPreload()
                }
            }

            else -> {
                playerManager.pause()
                playerManager.clearPreload()
            }
        }
    }

    DisposableEffect(lifecycleOwner, playerManager) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                playerManager.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            playerManager.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when {
            uiState.items.isEmpty() && uiState.isLoading -> {
                // Skeleton loading screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // Animated logo placeholder
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = Color.White.copy(alpha = 0.7f),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Text(
                        text = "正在加载",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    Text(
                        text = "精彩内容即将呈现",
                        color = Color.White.copy(alpha = 0.35f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }

            uiState.items.isEmpty() -> {
                FeedStateMessage(
                    title = "暂无内容",
                    description = "稍后再试",
                    showProgress = false,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (val feedItem = uiState.items[page]) {
                        is FeedItem.Video -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                VideoFeedCard(
                                    item = feedItem.item,
                                    isActive = page == pagerState.currentPage,
                                    playerManager = playerManager,
                                    isLandscapeFullscreen = isLandscapeFullscreen,
                                    onLandscapeToggle = { landscape ->
                                        isLandscapeFullscreen = landscape
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                )
                                if (!isLandscapeFullscreen) {
                                    FeedOverlay(
                                        authorName = feedItem.item.authorName,
                                        authorAvatar = feedItem.item.authorAvatar,
                                        title = feedItem.item.title,
                                        description = feedItem.item.description,
                                        likeCount = feedItem.item.likeCount,
                                        commentCount = feedItem.item.commentCount,
                                        collectCount = feedItem.item.collectCount,
                                        shareCount = feedItem.item.shareCount,
                                        recommendWords = recommendWordEngine.buildRecommendWords(feedItem),
                                        onSearchClick = onSearchClick,
                                        onRecommendWordClick = { word ->
                                            onRecommendWordClick(Uri.encode(word))
                                        },
                                    )
                                }
                            }
                        }

                        is FeedItem.ImageText -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                ImageTextFeedCard(
                                    item = feedItem.item,
                                    modifier = Modifier.fillMaxSize(),
                                )
                                if (!isLandscapeFullscreen) {
                                    FeedOverlay(
                                    authorName = feedItem.item.authorName,
                                    authorAvatar = feedItem.item.authorAvatar,
                                    title = feedItem.item.title,
                                    description = feedItem.item.description,
                                    likeCount = feedItem.item.likeCount,
                                    commentCount = feedItem.item.commentCount,
                                    collectCount = feedItem.item.collectCount,
                                    shareCount = feedItem.item.shareCount,
                                    recommendWords = recommendWordEngine.buildRecommendWords(feedItem),
                                    onSearchClick = onSearchClick,
                                    onRecommendWordClick = { word ->
                                        onRecommendWordClick(Uri.encode(word))
                                    },
                                )
                                } // end if (!isLandscapeFullscreen)
                            }
                        }
                    }
                }
            }
        }

        if (!isLandscapeFullscreen) {
            if (uiState.isLoading && uiState.items.isNotEmpty()) {
                Text(
                    text = "加载中...",
                    color = Color.White.copy(alpha = 0.86f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                )
            }

            // ── 分类标签栏 ──
        if (!isLandscapeFullscreen && uiState.items.isNotEmpty()) {
            CategoryTabRow(
                selectedIndex = selectedTab,
                onTabSelected = { tabIndex ->
                    selectedTab = tabIndex
                    val category = listOf("推荐", "西瓜视频", "热点", "社会", "娱乐", "科技", "财经", "体育")[tabIndex]
                    actualViewModel.selectCategory(category)
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding(),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 58.dp, end = 12.dp),
        ) {
            MetricsDebugToggle(
                showDebugMetrics = showDebugMetrics,
                onToggle = { showDebugMetrics = !showDebugMetrics },
            )
            PreloadToggle(
                preloadEnabled = preloadEnabled,
                onToggle = { preloadEnabled = !preloadEnabled },
            )
            DataSourceToggle(
                useRealData = useRealData,
                onToggle = {
                    // 循环: fake → verified → pexels → fake
                    AppConfig.dataSource = when (AppConfig.dataSource) {
                        "fake" -> "verified"
                        "verified" -> "pexels"
                        else -> "fake"
                    }
                    useRealData = AppConfig.dataSource != "fake"
                },
            )
        }

            if (showDebugMetrics) {
                PlaybackMetricsPanel(
                    metrics = playerManager.getPlaybackMetrics(),
                    onViewMoreClick = onMetricsClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 96.dp, end = 12.dp),
                )
            }
        } // end if (!isLandscapeFullscreen)
    }
}

@Composable
private fun MetricsDebugToggle(
    showDebugMetrics: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = if (showDebugMetrics) "隐藏指标" else "起播指标",
        color = Color.White,
        fontSize = 12.sp,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.48f))
            .clickable(onClick = onToggle)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun DataSourceToggle(
    useRealData: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = when (AppConfig.dataSource) {
        "pexels" -> "素材: Pexels"
        "verified" -> "素材: 已验证"
        else -> "素材: 测试"
    }
    val toggleColor = when (AppConfig.dataSource) {
        "pexels" -> Color(0xFF4CAF50)
        "verified" -> Color(0xFF2196F3)
        else -> Color(0xFFFF9800)
    }
    Text(
        text = label,
        color = toggleColor,
        fontSize = 12.sp,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.48f))
            .clickable(onClick = onToggle)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun PreloadToggle(
    preloadEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = if (preloadEnabled) "预加载: ON" else "预加载: OFF",
        color = if (preloadEnabled) Color(0xFF4CAF50) else Color(0xFFFF5722),
        fontSize = 12.sp,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.48f))
            .clickable(onClick = onToggle)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable
private fun PlaybackMetricsPanel(
    metrics: PlaybackMetrics?,
    onViewMoreClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.58f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        if (metrics == null) {
            Text(
                text = "暂无起播数据",
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
        } else {
            Text(
                text = """
                预加载命中：${if (metrics.isPreloaded) "是" else "否"}
                起播耗时：${metrics.displayStartMs} ms
                优化幅度：${metrics.improvementPercent}%
                """.trimIndent(),
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
            if (onViewMoreClick != null) {
                Text(
                    text = "📊 更多指标 →",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable(onClick = onViewMoreClick),
                )
            }
        }
    }
}

@Composable
private fun FeedStateMessage(
    title: String,
    description: String,
    showProgress: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showProgress) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.padding(bottom = 14.dp),
            )
        }
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = description,
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
