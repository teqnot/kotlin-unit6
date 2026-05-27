package com.example.authapp.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

object KtorClient {

    private const val BASE_URL = "https://dummyjson.com"

    var authToken: String? = null

    val instance: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                    explicitNulls = false
                    prettyPrint = true
                })
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            defaultRequest {
                url(BASE_URL)
                contentType(io.ktor.http.ContentType.Application.Json)

                authToken?.let { token ->
                    headers.append("Authorization", "Bearer $token")
                }
            }

            engine {
                config {
                    connectTimeout(15, TimeUnit.SECONDS)
                    readTimeout(15, TimeUnit.SECONDS)
                    writeTimeout(15, TimeUnit.SECONDS)
                }
            }
        }
    }
}