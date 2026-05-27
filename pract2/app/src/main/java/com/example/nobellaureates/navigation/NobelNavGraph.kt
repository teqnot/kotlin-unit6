package com.example.nobellaureates.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nobellaureates.presentation.ui.screens.LaureateDetailScreen
import com.example.nobellaureates.presentation.ui.screens.LaureateListScreen
import com.example.nobellaureates.presentation.viewmodel.NobelViewModel

const val ROUTE_LIST = "laureates"
const val ROUTE_DETAIL = "laureate"

@Composable
fun NobelNavGraph(
    navController: NavHostController,
    viewModel: NobelViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_LIST
    ) {
        composable(route = ROUTE_LIST) {
            LaureateListScreen(
                viewModel = viewModel,
                onLaureateClick = { laureateId ->
                    navController.navigate("$ROUTE_DETAIL/$laureateId")
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
            val laureateId = backStackEntry.arguments?.getString("id") ?: return@composable

            val laureate = viewModel.selectedLaureate.value
                ?: return@composable

            LaureateDetailScreen(
                laureate = laureate,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}