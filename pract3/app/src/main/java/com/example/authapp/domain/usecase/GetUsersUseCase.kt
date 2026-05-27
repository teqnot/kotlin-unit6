package com.example.authapp.domain.usecase

import com.example.authapp.domain.model.User
import com.example.authapp.domain.repository.AuthRepository


class GetUsersUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<List<User>> {
        return repository.getUsers()
    }
}