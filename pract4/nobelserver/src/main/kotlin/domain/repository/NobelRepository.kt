package com.example.domain.repository

import com.example.domain.model.Laureate
import com.example.domain.model.Prize
import com.example.domain.model.User


interface NobelRepository {
    suspend fun findUserByUsername(username: String): User?
    suspend fun createUser(user: User): User

    suspend fun getAllPrizes(): List<Prize>
    suspend fun getPrizeByYearAndCategory(year: Int, category: String): Prize?
    suspend fun savePrize(prize: Prize): Prize
    suspend fun syncPrizesFromExternal(): Int  // Возвращает количество добавленных

    suspend fun getLaureatesByPrize(prizeId: Int): List<Laureate>
    suspend fun saveLaureates(laureates: List<Laureate>): List<Laureate>

    suspend fun getUserFavorites(userId: Int): List<Prize>
    suspend fun addFavorite(userId: Int, prizeId: Int): Boolean
    suspend fun removeFavorite(userId: Int, prizeId: Int): Boolean
    suspend fun isFavorite(userId: Int, prizeId: Int): Boolean

    suspend fun authenticate(username: String, password: String): User?

    suspend fun getLaureatesByYearAndCategory(year: Int, category: String): List<Laureate>?
}