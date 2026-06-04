package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.data.MetricsRepository
import com.example.myapplication.ui.feed.FeedScreen
import com.example.myapplication.ui.metrics.MetricsDashboardScreen
import com.example.myapplication.ui.search.SearchResultScreen
import com.example.myapplication.ui.search.SearchScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val metricsRepository = remember {
        val database = AppDatabase.getDatabase(context)
        MetricsRepository(database.playbackMetricsDao())
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Feed,
    ) {
        composable(Routes.Feed) {
            FeedScreen(
                metricsRepository = metricsRepository,
                onSearchClick = {
                    navController.navigate(Routes.Search)
                },
                onRecommendWordClick = { keyword ->
                    navController.navigate("searchResult/$keyword")
                },
                onMetricsClick = {
                    navController.navigate(Routes.Metrics)
                },
            )
        }
        composable(
            route = Routes.FeedWithTarget,
            arguments = listOf(
                navArgument("targetId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
        ) { backStackEntry ->
            val targetId = backStackEntry.arguments?.getString("targetId")
            FeedScreen(
                targetId = targetId,
                metricsRepository = metricsRepository,
                onSearchClick = {
                    navController.navigate(Routes.Search)
                },
                onRecommendWordClick = { keyword ->
                    navController.navigate("searchResult/$keyword")
                },
                onMetricsClick = {
                    navController.navigate(Routes.Metrics)
                },
            )
        }
        composable(Routes.Search) {
            SearchScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSearch = { keyword ->
                    navController.navigate("searchResult/$keyword")
                },
            )
        }
        composable(
            route = Routes.SearchResult,
            arguments = listOf(
                navArgument("keyword") {
                    type = NavType.StringType
                }
            ),
        ) { backStackEntry ->
            val keyword = backStackEntry.arguments?.getString("keyword").orEmpty()
            SearchResultScreen(
                keyword = keyword,
                onBackClick = {
                    navController.popBackStack()
                },
                onVideoClick = { videoId ->
                    navController.navigate("feed?targetId=$videoId") {
                        popUpTo(Routes.Feed) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(Routes.Metrics) {
            MetricsDashboardScreen(
                metricsRepository = metricsRepository,
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
