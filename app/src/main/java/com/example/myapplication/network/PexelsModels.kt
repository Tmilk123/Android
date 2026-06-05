package com.example.myapplication.network

import com.google.gson.annotations.SerializedName

// ── Request ──

data class PexelsSearchResponse(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_results") val totalResults: Int,
    val videos: List<PexelsVideo>,
)

data class PexelsVideo(
    val id: Long,
    val url: String,
    val image: String?,
    val duration: Int,
    val user: PexelsUser,
    @SerializedName("video_files") val videoFiles: List<PexelsVideoFile>,
    @SerializedName("video_pictures") val videoPictures: List<PexelsVideoPicture>?,
)

data class PexelsUser(
    val name: String,
    val url: String?,
)

data class PexelsVideoFile(
    val id: Long,
    val quality: String,
    @SerializedName("file_type") val fileType: String,
    val width: Int,
    val height: Int,
    val link: String,
)

data class PexelsVideoPicture(
    val id: Long,
    val picture: String?,
    @SerializedName("nr") val nr: Int,
)
