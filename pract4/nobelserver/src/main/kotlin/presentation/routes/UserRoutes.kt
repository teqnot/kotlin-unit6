package com.example.presentation.routes

import com.example.domain.usecase.AddFavoriteUseCase
import com.example.domain.usecase.GetFavoritesUseCase
import com.example.domain.usecase.RemoveFavoriteUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.userRoutes(
    getFavoritesUseCase: GetFavoritesUseCase,
    addFavoriteUseCase: AddFavoriteUseCase,
    removeFavoriteUseCase: RemoveFavoriteUseCase
) {

    authenticate("auth-jwt") {

        // 🔹 GET /users/me — профиль
        get("/users/me") {
            val principal = call.principal<JWTPrincipal>() ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            val username = principal.payload.subject
            call.respond(HttpStatusCode.OK, mapOf(
                "username" to username,
                "roles" to principal.payload.getClaim("roles").asList(String::class.java)
            ))
        }

        get("/users/me/prizes") {
            try {
                val principal = call.principal<JWTPrincipal>() ?: run {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                // В реальном приложении: загрузить userId из БД по username
                val userId = 1  // 🔸 Заглушка для демо

                val favorites = getFavoritesUseCase(userId)
                call.respond(HttpStatusCode.OK, favorites.map { prize ->
                    mapOf(
                        "id" to prize.id,
                        "year" to prize.awardYear,
                        "category" to prize.category,
                        "fullName" to prize.fullName,
                        "motivation" to prize.motivation
                    )
                })

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // 🔹 POST /users/me/prizes/{prizeId} — добавить в избранное
        post("/users/me/prizes/{prizeId}") {
            try {
                val principal = call.principal<JWTPrincipal>() ?: run {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }

                val prizeId = call.parameters["prizeId"]?.toIntOrNull() ?: run {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid prizeId"))
                    return@post
                }

                val userId = 1  // 🔸 Заглушка

                val success = addFavoriteUseCase(userId, prizeId)

                if (success) {
                    call.respond(HttpStatusCode.Created, mapOf("message" to "Prize added to favorites"))
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Already in favorites"))
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        delete("/users/me/prizes/{prizeId}") {
            try {
                val prizeId = call.parameters["prizeId"]?.toIntOrNull() ?: run {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid prizeId"))
                    return@delete
                }

                val userId = 1  // 🔸 Заглушка

                val success = removeFavoriteUseCase(userId, prizeId)

                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Favorite not found"))
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}