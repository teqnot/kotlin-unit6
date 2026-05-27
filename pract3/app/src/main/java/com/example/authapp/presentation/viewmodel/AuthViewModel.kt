package com.example.authapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.domain.model.User
import com.example.authapp.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Authorized : AuthUiState()
    data class Success(val users: List<User>) : AuthUiState()
    data class UserDetail(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        _uiState.value = AuthUiState.Idle
    }

    fun login(username: String, password: String) {
        android.util.Log.d("AuthVM", "Login clicked: username=$username, password=${"*".repeat(password.length)}")

        viewModelScope.launch {
            android.util.Log.d("AuthVM", "Setting state to Loading")
            _uiState.value = AuthUiState.Loading

            loginUseCase(username, password)
                .onSuccess { result ->
                    android.util.Log.d("AuthVM", "Login success: user=${result.user.username}")
                    _currentUser.value = result.user
                    loadUsers()
                }
                .onFailure { error ->
                    android.util.Log.e("AuthVM", "Login failed: ${error.message}", error)
                    _uiState.value = AuthUiState.Error(
                        when {
                            error.message?.contains("401", ignoreCase = true) == true -> "Неверные учётные данные"
                            error.message?.contains("timeout", ignoreCase = true) == true -> "Превышено время ожидания"
                            error.message?.contains("accessToken", ignoreCase = true) == true -> "Ошибка парсинга ответа"
                            else -> "Ошибка: ${error.message}"
                        }
                    )
                }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            getUsersUseCase()
                .onSuccess { users ->
                    _uiState.value = AuthUiState.Success(users)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error("Ошибка загрузки: ${error.message}")
                }
        }
    }

    fun loadUserDetail(id: Int) {
        viewModelScope.launch {
            getUserDetailUseCase(id)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.UserDetail(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error("Ошибка загрузки: ${error.message}")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _currentUser.value = null
            _uiState.value = AuthUiState.Loading
        }
    }

    fun backToUsers() {
        loadUsers()
    }

    fun retry() {
        when (_uiState.value) {
            is AuthUiState.Success -> loadUsers()
            is AuthUiState.Error -> loadUsers()
            else -> { }
        }
    }
}