package com.example.data.model

import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizeDto(
    val year: Int,
    val category: String,
    val categoryFullName: String,
    val laureates: List<LaureateDto>,
    val overallMotivation: String? = null
)

@Serializable
data class LaureateDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val motivation: String,
    val share: String
)

fun NobelPrize.toDto() = NobelPrizeDto(
    year = year,
    category = category,
    categoryFullName = categoryFullName,
    laureates = laureates.map { it.toDto() },
    overallMotivation = overallMotivation
)

fun Laureate.toDto() = LaureateDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
    motivation = motivation,
    share = share
)