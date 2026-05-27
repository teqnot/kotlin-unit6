package com.example.presentation.routes

import com.example.data.model.toDto
import com.example.domain.usecase.GetLaureatesUseCase
import com.example.domain.usecase.GetPrizeByYearCategoryUseCase
import com.example.domain.usecase.GetPrizesUseCase
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.collections.map

fun Routing.prizeRoutes(
    getPrizesUseCase: GetPrizesUseCase,
    getPrizeByYearCategoryUseCase: GetPrizeByYearCategoryUseCase,
    getLaureatesUseCase: GetLaureatesUseCase
) {

    authenticate("auth-jwt") {

        get("/prizes") {
            try {
                val prizes = getPrizesUseCase()
                call.respond(HttpStatusCode.OK, prizes.map { it.toDto() })
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to fetch prizes: ${e.message}"))
            }
        }

        get("/prizes/{year}/{category}") {
            try {
                val year = call.parameters["year"]?.toIntOrNull()
                val category = call.parameters["category"]

                if (year == null || category.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid year or category"))
                    return@get
                }

                val prize = getPrizeByYearCategoryUseCase(year, category)

                if (prize == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Prize not found"))
                    return@get
                }

                call.respond(HttpStatusCode.OK, prize.toDto())

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to fetch prize: ${e.message}"))
            }
        }

        get("/prizes/{year}/{category}/laureates") {
            try {
                val year = call.parameters["year"]?.toIntOrNull()
                val category = call.parameters["category"]

                if (year == null || category.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid year or category"))
                    return@get
                }

                val laureates = getLaureatesUseCase(year, category)

                if (laureates == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Laureates not found"))
                    return@get
                }

                call.respond(HttpStatusCode.OK, laureates.map { laureate ->
                    mapOf(
                        "id" to (laureate.id ?: 0),
                        "fullName" to laureate.fullName,
                        "portion" to laureate.portion,
                        "motivation" to laureate.motivation,
                        "portraitUrl" to laureate.portraitUrl
                    )
                })

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to fetch laureates: ${e.message}"))
            }
        }
    }
}