package com.example.nobellaureates.data.network

import com.example.nobellaureates.data.model.LaureatesResponse
import com.example.nobellaureates.domain.model.NobelFilter
import io.ktor.client.call.body
import io.ktor.client.request.get

object NobelApi {

    suspend fun getPrizes(filter: NobelFilter): Result<PrizesResponse> {
        return try {
            val response: HttpResponse = KtorClient.instance.get("prizes") {
                url {
                }
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<PrizesResponse>())
            } else {
                Result.failure(Exception("Ошибка загрузки премий: ${response.status}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "Get prizes error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getPrizeDetail(year: Int, category: String): Result<PrizeDetailResponse> {
        return try {
            val response: HttpResponse = KtorClient.instance.get("prizes/$year/$category")

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<PrizeDetailResponse>())
            } else {
                Result.failure(Exception("Prize not found: ${response.status}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "Get prize detail error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getLaureates(year: Int, category: String): Result<List<LaureateResponse>> {
        return try {
            val response: HttpResponse = KtorClient.instance.get("prizes/$year/$category/laureates")

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Laureates not found: ${response.status}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "Get laureates error: ${e.message}", e)
            Result.failure(e)
        }
    }
}