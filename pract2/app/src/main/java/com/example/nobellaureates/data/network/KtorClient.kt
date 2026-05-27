package com.example.nobellaureates.data.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {

    private const val BASE_URL = "https://api.nobelprize.org/2.1/"

    val instance: HttpClient by lazy {
        HttpClient(CIO) {
            defaultRequest {
                url(BASE_URL)
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