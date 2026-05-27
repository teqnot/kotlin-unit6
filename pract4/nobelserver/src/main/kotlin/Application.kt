package com.example

import com.example.config.DatabaseConfig
import com.example.data.external.ExternalApiConfig
import com.example.data.external.NobelApiClient
import com.example.data.repository.NobelRepositoryImpl
import com.example.domain.usecase.AddFavoriteUseCase
import com.example.domain.usecase.AuthenticateUseCase
import com.example.domain.usecase.GetFavoritesUseCase
import com.example.domain.usecase.GetLaureatesUseCase
import com.example.domain.usecase.GetPrizeByYearCategoryUseCase
import com.example.domain.usecase.GetPrizesUseCase
import com.example.domain.usecase.RemoveFavoriteUseCase
import com.example.plugins.JwtConfig
import com.example.plugins.configureAuthentication
import com.example.plugins.configureCallLogging
import com.example.plugins.configureContentNegotiation
import com.example.plugins.configureDatabase
import com.example.plugins.configureRouting
import com.example.plugins.configureSwagger
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val config = environment.config

    val dbConfig = DatabaseConfig(
        driver = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5432/nobel_db",
        user = "nobel_user",
        password = "nobel_pass_123",
        poolSize = 10
    )
    val jwtConfig = JwtConfig(
        secret = "my-super-secret-key-for-jwt-signing-must-be-32-chars!",
        issuer = "nobel-api",
        audience = "nobel-clients",
        realm = "Nobel Prize API",
        tokenValiditySeconds = 1800
    )
    val externalConfig = ExternalApiConfig(
        baseUrl = "https://api.nobelprize.org/2.1",
        timeout = 30000
    )

    NobelApiClient.initialize(externalConfig)

    configureDatabase(dbConfig)

    val repository = NobelRepositoryImpl()

    val authenticateUseCase = AuthenticateUseCase(repository)
    val getPrizesUseCase = GetPrizesUseCase(repository)
    val getPrizeByYearCategoryUseCase = GetPrizeByYearCategoryUseCase(repository)
    val getLaureatesUseCase = GetLaureatesUseCase(repository)
    val getFavoritesUseCase = GetFavoritesUseCase(repository)
    val addFavoriteUseCase = AddFavoriteUseCase(repository)
    val removeFavoriteUseCase = RemoveFavoriteUseCase(repository)

    configureContentNegotiation()
    configureCallLogging()
    configureAuthentication(jwtConfig)
    configureSwagger()

    configureRouting(
        authenticateUseCase,
        getPrizesUseCase,
        getPrizeByYearCategoryUseCase,
        getLaureatesUseCase,
        getFavoritesUseCase,
        addFavoriteUseCase,
        removeFavoriteUseCase,
        jwtConfig
    )

    launch {
        try {
            val count = repository.syncPrizesFromExternal()
            println("Synced $count prizes from external API")

            val existingUser = repository.findUserByUsername("test")
            if (existingUser == null) {
                repository.createUser(
                    com.example.domain.model.User(
                        username = "test",
                        passwordHash = "test",
                        roles = listOf("user")
                    )
                )
                println("Test user created.")
            } else {
                println("Test user 'emilys' already exists.")
            }

        } catch (e: Exception) {
            println("Initialization error: ${e.message}")
            e.printStackTrace()
        }
    }
}