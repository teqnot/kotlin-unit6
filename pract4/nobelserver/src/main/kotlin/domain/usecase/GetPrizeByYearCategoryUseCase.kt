package com.example.domain.usecase

import com.example.domain.model.NobelPrize
import com.example.domain.repository.NobelRepository

class GetPrizeByYearCategoryUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(year: Int, category: String): NobelPrize? {
        return repository.getPrizeByYearAndCategory(year, category)
    }
}