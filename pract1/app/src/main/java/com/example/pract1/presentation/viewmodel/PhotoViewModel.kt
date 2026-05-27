package com.example.pract1.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract1.domain.model.Photo
import com.example.pract1.domain.usecase.GetPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PhotoUiState {
    object Loading : PhotoUiState()
    data class Success(val photos: List<Photo>) : PhotoUiState()
    data class Error(val message: String) : PhotoUiState()
}

class PhotoViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhotoUiState>(PhotoUiState.Loading)
    val uiState: StateFlow<PhotoUiState> = _uiState.asStateFlow()

    private val _selectedPhoto = MutableStateFlow<Photo?>(null)
    val selectedPhoto: StateFlow<Photo?> = _selectedPhoto.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _uiState.value = PhotoUiState.Loading
            getPhotosUseCase()
                .onSuccess { photos ->
                    _uiState.value = PhotoUiState.Success(photos)
                }
                .onFailure { error ->
                    _uiState.value = PhotoUiState.Error(error.message ?: "Неизвестная ошибка")
                }
        }
    }

    fun selectPhoto(photo: Photo) {
        _selectedPhoto.value = photo
    }

    fun clearSelection() {
        _selectedPhoto.value = null
    }

    fun retry() {
        loadPhotos()
    }
}