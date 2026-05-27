package com.example.data.repository

import com.example.data.datasource.InMemoryNobelDataSource
import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import com.example.domain.model.User
import com.example.domain.repository.NobelRepository

class NobelRepositoryImpl(
    private val dataSource: InMemoryNobelDataSource
) : NobelRepository {

    override suspend fun getAllPrizes(): List<NobelPrize> {
        return dataSource.getAllPrizes()
    }

    override suspend fun getPrizeByYearAndCategory(year: Int, category: String): NobelPrize? {
        return dataSource.getPrizeByYearAndCategory(year, category)
    }

    override suspend fun getLaureates(year: Int, category: String): List<Laureate>? {
        return dataSource.getLaureates(year, category)
    }

    override suspend fun findUser(username: String): User? {
        return dataSource.findUser(username)
    }

    override suspend fun authenticate(username: String, password: String): User? {
        val user = dataSource.findUser(username)
        return if (user?.passwordHash == password) user else null
    }
}