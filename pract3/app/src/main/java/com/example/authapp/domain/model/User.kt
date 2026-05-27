package com.example.authapp.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val gender: String,
    val image: String,
    val birthDate: String? = null,
    val phone: String? = null
)

data class AuthResult(
    val token: String,
    val user: User
)