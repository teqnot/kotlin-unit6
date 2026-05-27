package com.example.nobellaureates.domain.repository

import com.example.nobellaureates.domain.model.NobelFilter
import com.example.nobellaureates.domain.model.NobelLaureate

interface NobelRepository {
    suspend fun getLaureates(filter: NobelFilter): Result<List<NobelLaureate>>
    suspend fun getLaureateById(id: String): Result<NobelLaureate?>
}