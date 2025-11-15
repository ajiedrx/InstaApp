package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.domain.usecase.GetCurrentUserUseCase
import com.adr.instaapp.domain.usecase.GetUserPostsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Get current user
                val user = getCurrentUserUseCase()
                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User not found"
                    )
                    return@launch
                }

                // Get user posts
                getUserPostsUseCase(user.id).collect { posts ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user,
                        posts = posts,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }

    fun refresh() {
        loadProfileData()
    }

    data class ProfileUiState(
        val isLoading: Boolean = false,
        val user: com.adr.instaapp.domain.model.User? = null,
        val posts: List<com.adr.instaapp.domain.model.Post> = emptyList(),
        val error: String? = null
    )
}
