package com.example.pract1.domain.usecase

import com.example.pract1.domain.model.Photo
import com.example.pract1.domain.repository.PhotoRepository

class GetPhotosUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): Result<List<Photo>> = repository.getPhotos()
}