package com.example.nobellaureates.data.network

import com.example.nobellaureates.data.model.LaureatesResponse
import com.example.nobellaureates.domain.model.NobelFilter
import io.ktor.client.call.body
import io.ktor.client.request.get

object NobelApi {

    suspend fun getLaureates(filter: NobelFilter): Result<LaureatesResponse> {
        return try {
            val response = KtorClient.instance.get("laureates") {
                url {
                    parameters.append("limit", "100")
                    parameters.append("offset", "0")

                    filter.year?.let { parameters.append("nobelPrizeYear", it.toString()) }
                    if (filter.category != com.example.nobellaureates.domain.model.NobelCategory.ALL) {
                        parameters.append("nobelPrizeCategory", filter.category.apiValue)
                    }
                }
            }

            Result.success(response.body<LaureatesResponse>())
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "API Error: ${e.message}", e)
            Result.failure(e)
        }
    }
}