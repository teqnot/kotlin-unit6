package com.example.domain.model

data class User(
    val username: String,
    val passwordHash: String,
    val roles: List<String> = emptyList()
) {
    companion object {
        fun testUsers(): List<User> = listOf(
            User("emilys", "emilyspass", listOf("user")),
            User("admin", "admin123", listOf("admin", "user"))
        )
    }
}