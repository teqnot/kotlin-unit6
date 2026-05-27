package com.example.domain.model

data class NobelPrize(
    val year: Int,
    val category: String,
    val categoryFullName: String,
    val laureates: List<Laureate>,
    val overallMotivation: String? = null
) {
    val id: String get() = "${year}_${category}"
}