package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.usecase.CreatePostParams
import com.adr.instaapp.domain.usecase.CreatePostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostCreationViewModel(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostCreationUiState())
    val uiState: StateFlow<PostCreationUiState> = _uiState.asStateFlow()

    fun updateCaption(caption: String) {
        _uiState.value = _uiState.value.copy(caption = caption)
    }

    fun createPost() {
        val currentCaption = _uiState.value.caption
        if (currentCaption.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Caption cannot be empty"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isCreating = true,
                    error = null
                )

                val result = createPostUseCase(
                    CreatePostParams(
                        caption = currentCaption.trim(),
                        imageUrl = getRandomImageUrl()
                    )
                )

                result.fold(
                    onSuccess = { createdPost ->
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            isCreated = true,
                            createdPost = createdPost,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            error = error.message ?: "Failed to create post"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    error = e.message ?: "Unexpected error occurred"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = PostCreationUiState()
    }

    private fun getRandomImageUrl(): String {
        val randomImages = listOf(
            "https://picsum.photos/400/400?random=1",
            "https://picsum.photos/400/400?random=2",
            "https://picsum.photos/400/400?random=3",
            "https://picsum.photos/400/400?random=4",
            "https://picsum.photos/400/400?random=5"
        )
        return randomImages.random()
    }

    data class PostCreationUiState(
        val caption: String = "",
        val isCreating: Boolean = false,
        val isCreated: Boolean = false,
        val createdPost: Post? = null,
        val error: String? = null
    )
}
