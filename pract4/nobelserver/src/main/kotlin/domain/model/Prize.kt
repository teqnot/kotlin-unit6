package com.example.domain.model

data class Prize(
    val id: Int? = null,
    val awardYear: Int,
    val category: String,
    val fullName: String,
    val motivation: String?,
    val detailLink: String?,
    val laureates: List<Laureate> = emptyList()
) {
    val uniqueKey: String get() = "${awardYear}_$category"
}