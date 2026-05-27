package com.example.domain.usecase

import com.example.domain.model.Prize
import com.example.domain.repository.NobelRepository


class GetFavoritesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(userId: Int): List<Prize> {
        return repository.getUserFavorites(userId)
    }
}