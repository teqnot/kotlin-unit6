package com.example

import com.example.data.datasource.InMemoryNobelDataSource
import com.example.data.repository.NobelRepositoryImpl
import com.example.domain.usecase.AuthenticateUseCase
import com.example.domain.usecase.GetLaureatesUseCase
import com.example.domain.usecase.GetPrizeByYearCategoryUseCase
import com.example.domain.usecase.GetPrizesUseCase
import com.example.plugins.JwtConfig
import com.example.plugins.configureAuthentication
import com.example.plugins.configureCallLogging
import com.example.plugins.configureContentNegotiation
import com.example.plugins.configureRouting
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val config = ConfigFactory.load()
    val jwtConfig = JwtConfig.fromConfig(config)

    val dataSource = InMemoryNobelDataSource()
    val repository = NobelRepositoryImpl(dataSource)

    val authenticateUseCase = AuthenticateUseCase(repository)
    val getPrizesUseCase = GetPrizesUseCase(repository)
    val getPrizeByYearCategoryUseCase = GetPrizeByYearCategoryUseCase(repository)
    val getLaureatesUseCase = GetLaureatesUseCase(repository)

    configureContentNegotiation()
    configureCallLogging()
    configureAuthentication(jwtConfig)

    configureRouting(
        authenticateUseCase,
        getPrizesUseCase,
        getPrizeByYearCategoryUseCase,
        getLaureatesUseCase,
        jwtConfig
    )
}