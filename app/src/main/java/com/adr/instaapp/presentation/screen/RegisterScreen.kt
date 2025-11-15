package com.adr.instaapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val bio: String = ""
)

sealed interface RegisterEvent {
    data object OnRegisterClick : RegisterEvent
    data object OnLoginClick : RegisterEvent
    data class OnEmailChange(val email: String) : RegisterEvent
    data class OnUsernameChange(val username: String) : RegisterEvent
    data class OnPasswordChange(val password: String) : RegisterEvent
    data class OnConfirmPasswordChange(val confirmPassword: String) : RegisterEvent
    data class OnBioChange(val bio: String) : RegisterEvent
}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join InstaApp today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { onEvent(RegisterEvent.OnEmailChange(it)) },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.error?.contains("email", ignoreCase = true) == true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username Field
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { onEvent(RegisterEvent.OnUsernameChange(it)) },
                label = { Text("Username") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.error?.contains("username", ignoreCase = true) == true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { onEvent(RegisterEvent.OnPasswordChange(it)) },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.error?.contains("password", ignoreCase = true) == true,
                supportingText = if (uiState.password.isNotEmpty() && uiState.password.length < 6) {
                    { Text("Password must be at least 6 characters") }
                } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { onEvent(RegisterEvent.OnConfirmPasswordChange(it)) },
                label = { Text("Confirm Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = (uiState.confirmPassword.isNotEmpty() && uiState.password != uiState.confirmPassword),
                supportingText = if (uiState.confirmPassword.isNotEmpty() && uiState.password != uiState.confirmPassword) {
                    { Text("Passwords do not match") }
                } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bio Field (Optional)
            OutlinedTextField(
                value = uiState.bio,
                onValueChange = { onEvent(RegisterEvent.OnBioChange(it)) },
                label = { Text("Bio (Optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = false,
                maxLines = 2,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Button(
                    onClick = { onEvent(RegisterEvent.OnRegisterClick) },
                    enabled = !uiState.isLoading &&
                            uiState.email.isNotBlank() &&
                            uiState.username.isNotBlank() &&
                            uiState.password.isNotBlank() &&
                            uiState.confirmPassword.isNotBlank() &&
                            uiState.password == uiState.confirmPassword &&
                            uiState.password.length >= 6,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            TextButton(
                onClick = { onEvent(RegisterEvent.OnLoginClick) },
                enabled = !uiState.isLoading
            ) {
                Text("Already have an account? Login")
            }
        }
    }
}
