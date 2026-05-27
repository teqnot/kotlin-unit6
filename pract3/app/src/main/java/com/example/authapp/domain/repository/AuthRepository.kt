package com.example.authapp.domain.repository

import com.example.authapp.domain.model.AuthResult
import com.example.authapp.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthResult>
    suspend fun getUsers(): Result<List<User>>
    suspend fun getUserById(id: Int): Result<User>
    suspend fun logout()
    suspend fun loadSavedToken(): String?
}