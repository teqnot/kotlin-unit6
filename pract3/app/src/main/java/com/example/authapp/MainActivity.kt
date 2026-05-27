package com.example.authapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.authapp.data.preferences.TokenPreferences
import com.example.authapp.data.repository.AuthRepositoryImpl
import com.example.authapp.domain.usecase.*
import com.example.authapp.navigation.AuthNavGraph
import com.example.authapp.presentation.ui.theme.AuthAppTheme
import com.example.authapp.presentation.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenPreferences = TokenPreferences(this)
        val repository = AuthRepositoryImpl(tokenPreferences)

        val loginUseCase = LoginUseCase(repository)
        val getUsersUseCase = GetUsersUseCase(repository)
        val getUserDetailUseCase = GetUserDetailUseCase(repository)
        val logoutUseCase = LogoutUseCase(repository)

        setContent {
            AuthAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val viewModel: AuthViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return AuthViewModel(
                                    loginUseCase,
                                    getUsersUseCase,
                                    getUserDetailUseCase,
                                    logoutUseCase
                                ) as T
                            }
                        }
                    )

                    AuthNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}