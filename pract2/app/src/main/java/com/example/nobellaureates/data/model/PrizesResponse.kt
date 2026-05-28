package com.example.nobellaureates.data.model

import kotlinx.serialization.Serializable

typealias PrizesResponse = List<PrizeResponse>

@Serializable
data class PrizeResponse(
    val id: Int,
    val year: Int,
    val category: String,
    val fullName: String,
    val motivation: String?,
    val detailLink: String?,
    val laureates: List<LaureateResponse> = emptyList()
)

@Serializable
data class LaureateResponse(
    val id: Int,
    val fullName: String,
    val portion: String,
    val motivation: String?,
    val portraitUrl: String?
)

typealias PrizeDetailResponse = PrizeResponse
