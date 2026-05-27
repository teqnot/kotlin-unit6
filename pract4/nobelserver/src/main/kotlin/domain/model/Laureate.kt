package com.example.domain.model

data class Laureate(
    val id: Int? = null,
    val prizeId: Int,
    val fullName: String,
    val portion: String = "1",
    val motivation: String?,
    val portraitUrl: String?
)