package com.example.nobellaureates.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nobellaureates.presentation.ui.components.ErrorView
import com.example.nobellaureates.presentation.ui.components.FilterBar
import com.example.nobellaureates.presentation.ui.components.LaureateListItem
import com.example.nobellaureates.presentation.viewmodel.NobelUiState
import com.example.nobellaureates.presentation.viewmodel.NobelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaureateListScreen(
    viewModel: NobelViewModel,
    onLaureateClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🏆 Нобелевские лауреаты",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🔹 Фильтры
            FilterBar(
                currentFilter = filter,
                availableYears = viewModel.availableYears,
                availableCategories = viewModel.availableCategories,
                onFilterChange = { viewModel.updateFilter(it) }
            )

            // 🔹 Список или состояние
            when (val state = uiState) {
                is NobelUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is NobelUiState.Success -> {
                    if (state.laureates.isEmpty()) {
                        EmptyLaureatesView()
                    } else {
                        LaureateList(
                            laureates = state.laureates,
                            onLaureateClick = { laureateId ->
                                viewModel.selectLaureate(
                                    state.laureates.find { it.id == laureateId }!!
                                )
                                onLaureateClick(laureateId)
                            }
                        )
                    }
                }

                is NobelUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyLaureatesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🏅", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Нет лауреатов",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Попробуйте изменить фильтры",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LaureateList(
    laureates: List<com.example.nobellaureates.domain.model.NobelLaureate>,
    onLaureateClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(laureates, key = { it.id }) { laureate ->
            LaureateListItem(
                laureate = laureate,
                onClick = { onLaureateClick(laureate.id) }
            )
        }
    }
}