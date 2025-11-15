package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.domain.usecase.RegisterParams
import com.adr.instaapp.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain: StateFlow<Boolean> = _navigateToMain

    fun register(email: String, username: String, password: String, bio: String = "") {
        if (email.isBlank() || username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Please fill in all required fields"
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            registerUseCase(RegisterParams(email, username, password, bio))
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    _navigateToMain.value = true
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Registration failed"
                    )
                }
        }
    }

    fun onNavigationHandled() {
        _navigateToMain.value = false
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    data class RegisterUiState(
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
