package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.domain.model.User
import com.adr.instaapp.domain.usecase.GetCurrentUserUseCase
import com.adr.instaapp.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isCheckingAuth = MutableStateFlow(true)
    val isCheckingAuth: StateFlow<Boolean> = _isCheckingAuth.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _isCheckingAuth.value = true
            val user = getCurrentUserUseCase()
            _currentUser.value = user
            _isCheckingAuth.value = false
        }
    }

    fun refreshAuthState() {
        checkAuthState()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase(Unit)
            _currentUser.value = null
        }
    }
}
