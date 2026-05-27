package com.example.pract1.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pract1.domain.model.Photo
import com.example.pract1.presentation.ui.components.ErrorView
import com.example.pract1.presentation.ui.components.PhotoGridItem
import com.example.pract1.presentation.viewmodel.PhotoUiState
import com.example.pract1.presentation.viewmodel.PhotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    viewModel: PhotoViewModel,
    onPhotoClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "📸 Фотокаталог",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is PhotoUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PhotoUiState.Success -> {
                if (state.photos.isEmpty()) {
                    EmptyPhotosView()
                } else {
                    PhotoGrid(
                        photos = state.photos,
                        onPhotoClick = { photoId ->
                            viewModel.selectPhoto(state.photos.find { it.id == photoId }!!)
                            onPhotoClick(photoId)
                        }
                    )
                }
            }

            is PhotoUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun EmptyPhotosView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🖼️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Нет фотографий",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun PhotoGrid(
    photos: List<Photo>,
    onPhotoClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos, key = { it.id }) { photo ->
            PhotoGridItem(
                photo = photo,
                onClick = { onPhotoClick(photo.id) }
            )
        }
    }
}