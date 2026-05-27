package com.example.domain.usecase

import com.example.domain.model.User
import com.example.domain.repository.NobelRepository

class AuthenticateUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(username: String, password: String): User? {
        return repository.authenticate(username, password)
    }
}