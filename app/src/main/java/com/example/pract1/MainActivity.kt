package com.example.pract1

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
import com.example.pract1.data.repository.PhotoRepositoryImpl
import com.example.pract1.domain.usecase.GetPhotosUseCase
import com.example.pract1.navigation.PhotoNavGraph
import com.example.pract1.presentation.ui.theme.Pract1Theme
import com.example.pract1.presentation.viewmodel.PhotoViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = PhotoRepositoryImpl()
        val getPhotosUseCase = GetPhotosUseCase(repository)

        setContent {
            Pract1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val viewModel: PhotoViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return PhotoViewModel(getPhotosUseCase) as T
                            }
                        }
                    )

                    PhotoNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}