package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configureSwagger() {
    routing {
        get("/swagger") {
            call.respondRedirect("https://petstore.swagger.io/?url=http://localhost:8080/openapi/documentation.yaml")
        }

        get("/openapi/documentation.yaml") {
            call.respondText(
                text = generateOpenApiSpec(),
                contentType = io.ktor.http.ContentType.Application.Yaml
            )
        }
    }
}

private fun generateOpenApiSpec(): String {
    return """
        openapi: 3.0.3
        info:
          title: Nobel Prize API
          description: API для работы с данными Нобелевских премий
          version: 1.0.0
        servers:
          - url: http://localhost:8080
        components:
          securitySchemes:
            bearerAuth:
              type: http
              scheme: bearer
              bearerFormat: JWT
        paths:
          /auth/login:
            post:
              summary: Авторизация
              tags: [Auth]
              requestBody:
                required: true
                content:
                  application/json:
                    schema:
                      ${'$'}ref: '#/components/schemas/LoginRequest'
              responses:
                '200':
                  description: Успешная авторизация
                  content:
                    application/json:
                      schema:
                        ${'$'}ref: '#/components/schemas/AuthResponse'
          /prizes:
            get:
              summary: Список всех премий
              tags: [Prizes]
              security: [{ bearerAuth: [] }]
              responses:
                '200':
                  description: Список премий
          /users/me:
            get:
              summary: Профиль текущего пользователя
              tags: [Users]
              security: [{ bearerAuth: [] }]
              responses:
                '200':
                  description: Данные пользователя
          /users/me/prizes:
            get:
              summary: Избранные премии
              tags: [Users, Favorites]
              security: [{ bearerAuth: [] }]
              responses:
                '200':
                  description: Список избранных премий
            post:
              summary: Добавить премию в избранное
              tags: [Users, Favorites]
              security: [{ bearerAuth: [] }]
              parameters:
                - name: prizeId
                  in: path
                  required: true
                  schema: { type: integer }
              responses:
                '201':
                  description: Премия добавлена
            delete:
              summary: Удалить премию из избранного
              tags: [Users, Favorites]
              security: [{ bearerAuth: [] }]
              parameters:
                - name: prizeId
                  in: path
                  required: true
                  schema: { type: integer }
              responses:
                '204':
                  description: Премия удалена
        components:
          schemas:
            LoginRequest:
              type: object
              required: [username, password]
              properties:
                username: { type: string }
                password: { type: string }
            AuthResponse:
              type: object
              properties:
                accessToken: { type: string }
                tokenType: { type: string, default: "Bearer" }
                expiresIn: { type: integer }
                user:
                  type: object
                  properties:
                    username: { type: string }
                    roles: { type: array, items: { type: string } }
    """.trimIndent()
}