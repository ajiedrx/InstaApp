package com.adr.instaapp.domain.usecase

import com.adr.instaapp.data.repository.UserRepositoryImpl
import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.User
import com.adr.instaapp.domain.repository.UserRepository

data class RegisterParams(
    val email: String,
    val username: String,
    val password: String,
    val bio: String = ""
)

class RegisterUseCase(
    private val userRepository: UserRepository
) : UseCase<RegisterParams, User>() {

    override suspend fun invoke(parameters: RegisterParams): Result<User> {
        // Validate input
        if (parameters.email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        if (parameters.username.isBlank()) {
            return Result.failure(Exception("Username cannot be empty"))
        }
        if (parameters.password.isBlank()) {
            return Result.failure(Exception("Password cannot be empty"))
        }
        if (parameters.password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        if (!isValidEmail(parameters.email)) {
            return Result.failure(Exception("Please enter a valid email address"))
        }

        // Cast to UserRepositoryImpl to access registerWithEmail method
        val userRepositoryImpl =
            userRepository as? UserRepositoryImpl
                ?: return Result.failure(Exception("Invalid repository implementation"))

        return userRepositoryImpl.registerWithEmail(
            email = parameters.email,
            username = parameters.username,
            password = parameters.password,
            bio = parameters.bio
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
