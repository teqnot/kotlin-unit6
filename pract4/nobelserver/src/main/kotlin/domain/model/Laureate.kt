package com.example.domain.model

data class Laureate(
    val id: String,
    val firstName: String,
    val lastName: String,
    val motivation: String,
    val share: String = "1"
)