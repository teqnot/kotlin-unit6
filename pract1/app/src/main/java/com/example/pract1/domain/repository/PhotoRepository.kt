package com.example.pract1.domain.repository

import com.example.pract1.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): Result<List<Photo>>
}