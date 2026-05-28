package com.example.nobellaureates.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Int,
    val user: UserInfo
)

@Serializable
data class UserInfo(
    val username: String,
    val roles: List<String>
)