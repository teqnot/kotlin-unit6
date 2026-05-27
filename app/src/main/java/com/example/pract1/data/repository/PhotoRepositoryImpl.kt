package com.example.pract1.data.repository

import com.example.pract1.data.network.RetrofitClient
import com.example.pract1.domain.model.Photo
import com.example.pract1.domain.repository.PhotoRepository
import kotlin.collections.map


class PhotoRepositoryImpl : PhotoRepository {

    override suspend fun getPhotos(): Result<List<Photo>> {
        return try {
            val dtos = RetrofitClient.api.getPhotos()
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}