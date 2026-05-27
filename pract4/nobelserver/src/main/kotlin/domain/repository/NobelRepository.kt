package com.example.domain.repository

import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import com.example.domain.model.User

interface NobelRepository {
    suspend fun getAllPrizes(): List<NobelPrize>
    suspend fun getPrizeByYearAndCategory(year: Int, category: String): NobelPrize?
    suspend fun getLaureates(year: Int, category: String): List<Laureate>?
    suspend fun findUser(username: String): User?
    suspend fun authenticate(username: String, password: String): User?
}