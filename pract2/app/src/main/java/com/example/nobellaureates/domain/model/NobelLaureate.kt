package com.example.nobellaureates.domain.model

enum class NobelCategory(val apiValue: String, val displayName: String) {
    PHYSICS("physics", "Физика"),
    CHEMISTRY("chemistry", "Химия"),
    MEDICINE("medicine", "Медицина"),
    LITERATURE("literature", "Литература"),
    PEACE("peace", "Мир"),
    ECONOMICS("economics", "Экономика"),
    ALL("all", "Все");

    companion object {
        fun fromApiString(value: String): NobelCategory {
            return entries.find { it.apiValue == value.lowercase() } ?: ALL
        }
    }
}

data class NobelFilter(
    val year: Int? = null,
    val category: NobelCategory = NobelCategory.ALL
)

data class NobelLaureate(
    val id: String,
    val year: Int,
    val category: NobelCategory,
    val categoryName: String,
    val fullName: String,
    val motivation: String,
    val birthDate: String?,
    val birthPlace: String?,
    val portraitUrl: String?
) {
    val shortMotivation: String
        get() = if (motivation.length > 100) {
            motivation.take(100) + "..."
        } else motivation
}