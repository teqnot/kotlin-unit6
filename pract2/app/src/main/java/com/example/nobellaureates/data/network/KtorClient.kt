package com.example.nobellaureates.data.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {

    private const val BASE_URL = "http://10.0.2.2:8080"

    var authToken: String? = null

    val instance: HttpClient by lazy {
        HttpClient(CIO) {
            defaultRequest {
                url(BASE_URL)
                contentType(ContentType.Application.Json)

                authToken?.let { token ->
                    headers.append("Authorization", "Bearer $token")
                }
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                    explicitNulls = false
                })
            }

            engine {
                endpoint {
                    connectTimeout = 15000
                    socketTimeout = 15000
                }
            }
        }
    }
}