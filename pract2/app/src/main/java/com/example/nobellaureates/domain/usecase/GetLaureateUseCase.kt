package com.example.nobellaureates.domain.usecase

import com.example.nobellaureates.domain.model.NobelFilter
import com.example.nobellaureates.domain.model.NobelLaureate
import com.example.nobellaureates.domain.repository.NobelRepository

class GetLaureatesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(filter: NobelFilter): Result<List<NobelLaureate>> {
        return repository.getLaureates(filter)
    }
}