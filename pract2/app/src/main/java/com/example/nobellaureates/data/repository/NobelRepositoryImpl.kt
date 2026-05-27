package com.example.nobellaureates.data.repository

import com.example.nobellaureates.data.model.toDomain
import com.example.nobellaureates.data.network.NobelApi
import com.example.nobellaureates.domain.model.NobelFilter
import com.example.nobellaureates.domain.model.NobelLaureate
import com.example.nobellaureates.domain.repository.NobelRepository

class NobelRepositoryImpl : NobelRepository {

    override suspend fun getLaureates(filter: NobelFilter): Result<List<NobelLaureate>> {
        return NobelApi.getLaureates(filter)
            .map { response ->
                response.laureates.mapNotNull { it.toDomain() }
            }
    }

    override suspend fun getLaureateById(id: String): Result<NobelLaureate?> {
        return getLaureates(NobelFilter())
            .map { laureates -> laureates.find { it.id == id } }
    }
}