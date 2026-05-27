package com.example.nobellaureates.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nobellaureates.domain.model.NobelCategory
import com.example.nobellaureates.domain.model.NobelFilter
import com.example.nobellaureates.domain.model.NobelLaureate
import com.example.nobellaureates.domain.usecase.GetLaureateDetailUseCase
import com.example.nobellaureates.domain.usecase.GetLaureatesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NobelUiState {
    object Loading : NobelUiState()
    data class Success(val laureates: List<NobelLaureate>) : NobelUiState()
    data class Error(val message: String) : NobelUiState()
}

data class NobelUiFilter(
    val year: Int? = null,
    val category: NobelCategory = NobelCategory.ALL
) {
    fun toDomainFilter() = NobelFilter(year = year, category = category)
}

class NobelViewModel(
    private val getLaureatesUseCase: GetLaureatesUseCase,
    private val getLaureateDetailUseCase: GetLaureateDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NobelUiState>(NobelUiState.Loading)
    val uiState: StateFlow<NobelUiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(NobelUiFilter())
    val filter: StateFlow<NobelUiFilter> = _filter.asStateFlow()

    private val _selectedLaureate = MutableStateFlow<NobelLaureate?>(null)
    val selectedLaureate: StateFlow<NobelLaureate?> = _selectedLaureate.asStateFlow()

    val availableYears: List<Int> = (1901..2024).toList().reversed()

    val availableCategories: List<NobelCategory> = NobelCategory.entries

    init {
        loadLaureates()
    }

    fun loadLaureates() {
        viewModelScope.launch {
            _uiState.value = NobelUiState.Loading
            getLaureatesUseCase(_filter.value.toDomainFilter())
                .onSuccess { laureates ->
                    _uiState.value = NobelUiState.Success(laureates)
                }
                .onFailure { error ->
                    _uiState.value = NobelUiState.Error(error.message ?: "Неизвестная ошибка")
                }
        }
    }

    fun updateFilter(newFilter: NobelUiFilter) {
        _filter.value = newFilter
        loadLaureates()
    }

    fun selectLaureate(laureate: NobelLaureate) {
        _selectedLaureate.value = laureate
    }

    fun loadLaureateDetail(id: String) {
        viewModelScope.launch {
            getLaureateDetailUseCase(id)
                .onSuccess { laureate ->
                    _selectedLaureate.value = laureate
                }
                .onFailure { /* можно показать ошибку */ }
        }
    }

    fun clearSelection() {
        _selectedLaureate.value = null
    }

    fun retry() {
        loadLaureates()
    }
}