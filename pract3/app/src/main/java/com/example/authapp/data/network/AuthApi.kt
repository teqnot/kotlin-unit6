package com.example.authapp.data.network

import com.example.authapp.data.model.LoginRequest
import com.example.authapp.data.model.LoginResponse
import com.example.authapp.data.model.UserDto
import com.example.authapp.data.model.UsersResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

object AuthApi {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = KtorClient.instance.post("auth/login") {
                setBody(LoginRequest(username, password))
            }

            if (response.status.value in 200..299) {
                Result.success(response.body<LoginResponse>())
            } else {
                Result.failure(Exception("Ошибка авторизации: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsers(limit: Int = 100, skip: Int = 0): Result<UsersResponse> {
        return try {
            val response = KtorClient.instance.get("users") {
                url {
                    parameters.append("limit", limit.toString())
                    parameters.append("skip", skip.toString())
                }
            }

            if (response.status.value in 200..299) {
                Result.success(response.body<UsersResponse>())
            } else {
                Result.failure(Exception("Ошибка загрузки: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Int): Result<UserDto> {
        return try {
            val response = KtorClient.instance.get("users/$id")

            if (response.status.value in 200..299) {
                Result.success(response.body<UserDto>())
            } else {
                Result.failure(Exception("Ошибка загрузки: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}