package com.example.pract1.data.model

import com.example.pract1.domain.model.Photo
import com.google.gson.annotations.SerializedName

data class PhotoDto(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    @SerializedName("download_url")
    val downloadUrl: String
) {
    fun toDomain() = Photo(
        id = id,
        author = author,
        width = width,
        height = height,
        thumbnailUrl = "$url?w=200&h=200",
        fullImageUrl = downloadUrl,
        pageUrl = url
    )
}