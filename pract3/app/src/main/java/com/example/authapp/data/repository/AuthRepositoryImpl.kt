package com.example.authapp.data.repository

import com.example.authapp.data.model.toDomain
import com.example.authapp.data.network.AuthApi
import com.example.authapp.data.network.KtorClient
import com.example.authapp.data.preferences.TokenPreferences
import com.example.authapp.domain.model.AuthResult
import com.example.authapp.domain.model.User
import com.example.authapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class AuthRepositoryImpl(
    private val tokenPreferences: TokenPreferences
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<AuthResult> {
        return try {
            android.util.Log.d("AuthRepo", "Login attempt: username=$username")

            val response = AuthApi.login(username, password)

            response.onSuccess { loginResponse ->
                android.util.Log.d("AuthRepo", "Login success: token=${loginResponse.accessToken.take(20)}...")

                tokenPreferences.saveToken(loginResponse.accessToken)
                tokenPreferences.saveUserData(loginResponse.id, loginResponse.username)

                KtorClient.authToken = loginResponse.accessToken
            }

            response.map { loginResponse ->
                AuthResult(
                    token = loginResponse.accessToken,
                    user = User(
                        id = loginResponse.id,
                        username = loginResponse.username,
                        email = loginResponse.email,
                        firstName = loginResponse.firstName,
                        lastName = loginResponse.lastName,
                        fullName = "${loginResponse.firstName} ${loginResponse.lastName}",
                        gender = loginResponse.gender,
                        image = loginResponse.image
                    )
                )
            }

        } catch (e: Exception) {
            android.util.Log.e("AuthRepo", "Login error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return try {
            val response = AuthApi.getUsers()
            response.map { it.users.map { dto -> dto.toDomain() } }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(id: Int): Result<User> {
        return try {
            val response = AuthApi.getUserById(id)
            response.map { it.toDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenPreferences.clearToken()
        KtorClient.authToken = null
    }

    override suspend fun loadSavedToken(): String? {
        return tokenPreferences.tokenFlow.firstOrNull()
    }
}