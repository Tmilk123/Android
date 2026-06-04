package com.example.myapplication.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesTest {

    @Test
    fun routesExposeExpectedPaths() {
        assertEquals("feed", Routes.Feed)
        assertEquals("feed?targetId={targetId}", Routes.FeedWithTarget)
        assertEquals("search", Routes.Search)
        assertEquals("searchResult/{keyword}", Routes.SearchResult)
    }
}
