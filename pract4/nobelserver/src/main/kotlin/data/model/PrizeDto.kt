package com.example.data.model

import com.example.domain.model.Laureate
import com.example.domain.model.Prize
import kotlinx.serialization.Serializable

@Serializable
data class PrizeDto(
    val id: Int,
    val year: Int,
    val category: String,
    val fullName: String,
    val motivation: String?,
    val detailLink: String?,
    val laureates: List<LaureateDto> = emptyList()
)

@Serializable
data class LaureateDto(
    val id: Int,
    val fullName: String,
    val portion: String,
    val motivation: String?,
    val portraitUrl: String?
)

fun Prize.toDto() = PrizeDto(
    id = id ?: 0,
    year = awardYear,
    category = category,
    fullName = fullName,
    motivation = motivation,
    detailLink = detailLink,
    laureates = laureates.map { it.toDto() }
)

fun Laureate.toDto() = LaureateDto(
    id = id ?: 0,
    fullName = fullName,
    portion = portion,
    motivation = motivation,
    portraitUrl = portraitUrl
)