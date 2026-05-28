object AuthApi {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response: HttpResponse = KtorClient.instance.post("auth/login") {
                setBody(LoginRequest(username, password))
            }

            if (response.status == HttpStatusCode.OK) {
                val loginResponse = response.body<LoginResponse>()
                KtorClient.authToken = loginResponse.accessToken
                Result.success(loginResponse)
            } else {
                Result.failure(Exception("Ошибка авторизации: ${response.status}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthApi", "Login error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun logout() {
        KtorClient.authToken = null
    }
}