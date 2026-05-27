package com.example.pract1.data.network

import com.example.pract1.data.model.PhotoDto
import retrofit2.http.GET


interface PicsumApi {
    @GET("v2/list?page=1&limit=50")
    suspend fun getPhotos(): List<PhotoDto>
}