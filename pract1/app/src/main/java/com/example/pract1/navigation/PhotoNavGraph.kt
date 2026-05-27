package com.example.pract1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pract1.presentation.ui.screens.PhotoDetailScreen
import com.example.pract1.presentation.ui.screens.PhotoListScreen
import com.example.pract1.presentation.viewmodel.PhotoViewModel

const val ROUTE_LIST = "photos"
const val ROUTE_DETAIL = "photo"
@Composable
fun PhotoNavGraph(
    navController: NavHostController,
    viewModel: PhotoViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_LIST
    ) {
        composable(route = ROUTE_LIST) {
            PhotoListScreen(
                viewModel = viewModel,
                onPhotoClick = { photoId ->
                    navController.navigate("$ROUTE_DETAIL/$photoId")
                }
            )
        }

        composable(
            route = "$ROUTE_DETAIL/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("id") ?: return@composable

            val photo = viewModel.selectedPhoto.value
                ?.takeIf { it.id == photoId }
                ?: return@composable

            PhotoDetailScreen(
                photo = photo,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}