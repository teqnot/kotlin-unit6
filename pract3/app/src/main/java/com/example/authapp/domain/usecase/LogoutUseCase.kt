package com.example.authapp.domain.usecase

import com.example.authapp.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}