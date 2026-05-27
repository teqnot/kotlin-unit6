package com.example.pract1.domain.model

data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val thumbnailUrl: String,
    val fullImageUrl: String,
    val pageUrl: String
)