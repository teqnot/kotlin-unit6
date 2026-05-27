package com.example.plugins

import com.example.domain.usecase.AuthenticateUseCase
import com.example.domain.usecase.GetLaureatesUseCase
import com.example.domain.usecase.GetPrizeByYearCategoryUseCase
import com.example.domain.usecase.GetPrizesUseCase
import com.example.presentation.routes.authRoutes
import com.example.presentation.routes.prizeRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    authenticateUseCase: AuthenticateUseCase,
    getPrizesUseCase: GetPrizesUseCase,
    getPrizeByYearCategoryUseCase: GetPrizeByYearCategoryUseCase,
    getLaureatesUseCase: GetLaureatesUseCase,
    jwtConfig: JwtConfig
) {
    routing {
        get("/") {
            call.respondText("Nobel Prize API v1.0 - Use /auth/login to authenticate")
        }

        get("/health") {
            call.respond(mapOf("status" to "ok", "service" to "nobel-api"))
        }

        authRoutes(authenticateUseCase, jwtConfig)

        prizeRoutes(
            getPrizesUseCase,
            getPrizeByYearCategoryUseCase,
            getLaureatesUseCase
        )

        route("{...}") {
            handle {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Endpoint not found"))
            }
        }
    }
}