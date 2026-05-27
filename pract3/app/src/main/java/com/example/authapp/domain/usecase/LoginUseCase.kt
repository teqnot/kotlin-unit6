package com.example.authapp.domain.usecase

import com.example.authapp.domain.model.AuthResult
import com.example.authapp.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Result<AuthResult> {
        return repository.login(username, password)
    }
}