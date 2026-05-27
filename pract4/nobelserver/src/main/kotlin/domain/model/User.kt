package com.example.domain.model

data class User(
    val id: Int? = null,
    val username: String,
    val passwordHash: String,
    val roles: List<String> = listOf("user")
) {
    val primaryRole: String get() = roles.firstOrNull() ?: "user"
}