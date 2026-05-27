package com.example.domain.usecase

import com.example.domain.model.NobelPrize
import com.example.domain.repository.NobelRepository

class GetPrizesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(): List<NobelPrize> {
        return repository.getAllPrizes()
    }
}