package com.example.authapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.authapp.presentation.ui.screens.*
import com.example.authapp.presentation.viewmodel.AuthViewModel

const val ROUTE_LOGIN = "login"
const val ROUTE_USERS = "users"
const val ROUTE_USER_DETAIL_BASE = "user"

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    viewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_LOGIN
    ) {
        composable(route = ROUTE_LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(ROUTE_USERS) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(route = ROUTE_USERS) {
            UsersListScreen(
                viewModel = viewModel,
                onUserClick = { userId ->
                    navController.navigate("$ROUTE_USER_DETAIL_BASE/$userId")
                },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "$ROUTE_USER_DETAIL_BASE/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("id") ?: return@composable

            UserDetailScreen(
                user = viewModel.currentUser.value ?: return@composable,
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    viewModel.logout()
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }
    }
}