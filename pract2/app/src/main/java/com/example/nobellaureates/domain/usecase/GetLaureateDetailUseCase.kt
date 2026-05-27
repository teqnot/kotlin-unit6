package com.example.nobellaureates.domain.usecase

import com.example.nobellaureates.domain.model.NobelLaureate
import com.example.nobellaureates.domain.repository.NobelRepository

class GetLaureateDetailUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(id: String): Result<NobelLaureate?> {
        return repository.getLaureateById(id)
    }
}