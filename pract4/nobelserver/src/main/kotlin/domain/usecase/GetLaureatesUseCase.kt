package com.example.domain.usecase

import com.example.domain.model.Laureate
import com.example.domain.repository.NobelRepository

class GetLaureatesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(year: Int, category: String): List<Laureate>? {
        return repository.getLaureates(year, category)
    }
}