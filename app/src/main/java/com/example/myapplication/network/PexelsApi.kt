package com.example.myapplication.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApi {

    @GET("videos/search")
    suspend fun searchVideos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
    ): PexelsSearchResponse

    @GET("videos/popular")
    suspend fun popularVideos(
        @Header("Authorization") apiKey: String,
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1,
    ): PexelsSearchResponse

    companion object {
        const val BASE_URL = "https://api.pexels.com/"
    }
}
