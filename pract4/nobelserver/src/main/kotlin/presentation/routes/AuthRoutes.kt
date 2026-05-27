package com.example.presentation.routes

import com.example.data.model.AuthResponse
import com.example.data.model.LoginRequest
import com.example.data.model.UserInfo
import com.example.domain.usecase.AuthenticateUseCase
import com.example.plugins.JwtConfig
import com.example.plugins.generateToken
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.authRoutes(authenticateUseCase: AuthenticateUseCase, jwtConfig: JwtConfig) {

    post("/auth/login") {
        try {
            val request = call.receive<LoginRequest>()

            if (request.username.isBlank() || request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username and password are required"))
                return@post
            }

            val user = authenticateUseCase(request.username, request.password)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                return@post
            }

            val token = generateToken(user, jwtConfig)

            call.respond(HttpStatusCode.OK, AuthResponse(
                accessToken = token,
                expiresIn = jwtConfig.tokenValiditySeconds.toInt(),
                user = UserInfo(user.username, user.roles)
            )
            )

        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Authentication failed: ${e.message}"))
        }
    }
}