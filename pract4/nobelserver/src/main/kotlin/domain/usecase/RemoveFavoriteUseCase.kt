package com.example.domain.usecase

import com.example.domain.repository.NobelRepository

class RemoveFavoriteUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(userId: Int, prizeId: Int): Boolean {
        return repository.removeFavorite(userId, prizeId)
    }
}