package com.example.authapp.domain.usecase

import com.example.authapp.domain.model.User
import com.example.authapp.domain.repository.AuthRepository

class GetUserDetailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(id: Int): Result<User> {
        return repository.getUserById(id)
    }
}