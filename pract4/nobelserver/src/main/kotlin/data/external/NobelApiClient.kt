package com.example.data.external

import com.example.domain.model.Laureate
import com.example.domain.model.Prize
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.ApplicationConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ExternalPrizeResponse(
    val nobelPrizes: List<ExternalPrizeDto>
)

@Serializable
data class ExternalPrizeDto(
    val awardYear: Int,

    val category: NameDto? = null,

    @SerialName("categoryFullName")
    val categoryFullName: Map<String, String> = emptyMap(),

    val motivation: Map<String, String>? = null,
    val laureates: List<ExternalLaureateDto> = emptyList()
) {
    val categoryEnglish: String
        get() = category?.en ?: categoryFullName["en"] ?: "Unknown"
}

@Serializable
data class ExternalLaureateDto(
    val id: String,
    val firstName: NameDto? = null,
    val lastName: NameDto? = null,
    val motivation: Map<String, String>? = null,
    val portion: String? = null
) {
    val fullName: String
        get() = "${firstName?.en ?: ""} ${lastName?.en ?: ""}".trim().takeIf { it.isNotBlank() } ?: "Unknown"
}

@Serializable
data class NameDto(
    val en: String? = null,
    val no: String? = null,
    val se: String? = null,
    val original: String? = null
) {
    val english: String? get() = en ?: original
}

fun ExternalPrizeDto.toDomainPrize(): Prize {
    return Prize(
        awardYear = awardYear,
        category = categoryEnglish.lowercase(),
        fullName = categoryFullName["en"] ?: categoryEnglish,
        motivation = motivation?.get("en"),
        detailLink = null,  // Можно добавить позже
        laureates = laureates.map { it.toDomainLaureate() }
    )
}

fun ExternalLaureateDto.toDomainLaureate(): Laureate {
    return Laureate(
        prizeId = 0,
        fullName = fullName,
        portion = portion ?: "1",
        motivation = motivation?.get("en"),
        portraitUrl = null  // Можно добавить позже
    )
}

// 🔹 Клиент для внешнего API
object NobelApiClient {

    private lateinit var client: HttpClient
    private lateinit var config: ExternalApiConfig

    fun initialize(cfg: ExternalApiConfig) {
        config = cfg
        client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                    explicitNulls = false
                })
            }
        }
    }

    suspend fun fetchPrizes(year: Int? = null, category: String? = null): List<ExternalPrizeDto> {
        return try {
            val response = client.get("${config.baseUrl}/nobelPrizes") {
                url {
                    parameters.append("limit", "100")
                    year?.let { parameters.append("nobelPrizeYear", it.toString()) }
                    category?.let { parameters.append("nobelPrizeCategory", it) }
                }
                timeout {
                    socketTimeoutMillis = config.timeout
                }
            }

            if (response.status == HttpStatusCode.OK) {
                response.body<ExternalPrizeResponse>().nobelPrizes
            } else {
                println("❌ External API error: ${response.status}")
                emptyList()
            }
        } catch (e: kotlinx.serialization.SerializationException) {
            println("Serialization error: ${e.message}")
            println("Raw JSON preview: ${e.message?.substringAfter("JSON input: ")?.take(200)}")
            emptyList()
        } catch (e: Exception) {
            println("External API error: ${e.message}")
            emptyList()
        }
    }

    fun close() {
        client.close()
    }
}

data class ExternalApiConfig(
    val baseUrl: String,
    val timeout: Long
) {
    companion object {
        fun fromConfig(config: ApplicationConfig): ExternalApiConfig {
            val extConfig = config.config("external.nobelApi")
            return ExternalApiConfig(
                baseUrl = extConfig.property("baseUrl").getString(),
                timeout = extConfig.property("timeout").getString().toLong()
            )
        }
    }
}