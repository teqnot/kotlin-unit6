package com.example.domain.usecase

import com.example.domain.repository.NobelRepository

class AddFavoriteUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(userId: Int, prizeId: Int): Boolean {
        return repository.addFavorite(userId, prizeId)
    }
}