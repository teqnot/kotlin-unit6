package com.example.nobellaureates

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
import com.example.nobellaureates.data.repository.NobelRepositoryImpl
import com.example.nobellaureates.domain.usecase.GetLaureateDetailUseCase
import com.example.nobellaureates.domain.usecase.GetLaureatesUseCase
import com.example.nobellaureates.navigation.NobelNavGraph
import com.example.nobellaureates.presentation.ui.theme.NobelLaureatesTheme
import com.example.nobellaureates.presentation.viewmodel.NobelViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = NobelRepositoryImpl()
        val getLaureatesUseCase = GetLaureatesUseCase(repository)
        val getLaureateDetailUseCase = GetLaureateDetailUseCase(repository)

        setContent {
            NobelLaureatesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val viewModel: NobelViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return NobelViewModel(
                                    getLaureatesUseCase,
                                    getLaureateDetailUseCase
                                ) as T
                            }
                        }
                    )

                    NobelNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}