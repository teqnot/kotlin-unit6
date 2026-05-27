package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
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