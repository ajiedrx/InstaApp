package com.adr.instaapp.data.repository

import android.content.Context
import com.adr.instaapp.data.datasource.DummyDataSource
import com.adr.instaapp.data.local.AuthPreferencesManager
import com.adr.instaapp.data.model.UserCredentials
import com.adr.instaapp.domain.model.User
import com.adr.instaapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class UserRepositoryImpl(
    private val dataSource: DummyDataSource,
    context: Context
) : UserRepository {

    // Extended method for registration with email
    suspend fun registerWithEmail(
        email: String,
        username: String,
        password: String,
        bio: String
    ): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            // Check if username or email already exists
            if (authPreferencesManager.isUsernameTaken(username)) {
                return Result.failure(Exception("Username already taken"))
            }
            if (authPreferencesManager.isEmailTaken(email)) {
                return Result.failure(Exception("Email already taken"))
            }

            // Create new user credentials with unique ID
            val newUser = UserCredentials(
                id = System.currentTimeMillis(),
                email = email,
                username = username,
                password = password,
                bio = bio,
                profileImageUrl = "",
                createdAt = System.currentTimeMillis()
            )

            // Save user to SharedPreferences
            val saved = authPreferencesManager.saveUser(newUser)
            if (!saved) {
                return Result.failure(Exception("Username or email already exists"))
            }

            currentUser = mapCredentialsToUser(newUser)
            _currentUserFlow.tryEmit(currentUser)
            isLoggedIn = true

            // Save current session
            authPreferencesManager.saveCurrentUserId(newUser.id)

            Result.success(currentUser!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private val authPreferencesManager = AuthPreferencesManager(context)

    private val _currentUserFlow = MutableSharedFlow<User?>(
        replay = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    private var currentUser: User? = null
    private var isLoggedIn = false

    init {
        // Check for existing session on app start
        val currentUserCredentials = authPreferencesManager.getCurrentUser()
        if (currentUserCredentials != null) {
            currentUser = mapCredentialsToUser(currentUserCredentials)
            isLoggedIn = true
            _currentUserFlow.tryEmit(currentUser)
        } else {
            _currentUserFlow.tryEmit(null)
        }
    }

    override suspend fun getCurrentUser(): User? = currentUser

    override fun getCurrentUserFlow(): SharedFlow<User?> = _currentUserFlow

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            // Check if user exists in SharedPreferences
            val userCredentials = authPreferencesManager.validateCredentials(username, password)
            if (userCredentials == null) {
                return Result.failure(Exception("Username not found or incorrect password"))
            }

            currentUser = mapCredentialsToUser(userCredentials)
            _currentUserFlow.tryEmit(currentUser)
            isLoggedIn = true

            // Save current session
            authPreferencesManager.saveCurrentUserId(userCredentials.id)

            Result.success(currentUser!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, password: String, bio: String): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            // Check if username already exists
            if (authPreferencesManager.isUsernameTaken(username)) {
                return Result.failure(Exception("Username already taken"))
            }

            // Create new user credentials with unique ID
            val newUser = UserCredentials(
                id = System.currentTimeMillis(),
                email = "", // Email will be handled by RegisterUseCase
                username = username,
                password = password,
                bio = bio,
                profileImageUrl = "",
                createdAt = System.currentTimeMillis()
            )

            currentUser = mapCredentialsToUser(newUser)
            _currentUserFlow.tryEmit(currentUser)
            isLoggedIn = true

            Result.success(currentUser!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            dataSource.simulateNetworkDelay()
            currentUser = null
            _currentUserFlow.tryEmit(null)
            isLoggedIn = false

            // Clear current session from SharedPreferences
            authPreferencesManager.clearCurrentUser()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Map UserCredentials to domain User model
     */
    private fun mapCredentialsToUser(credentials: UserCredentials): User {
        return User(
            id = credentials.id.toString(),
            username = credentials.username,
            profilePictureUrl = credentials.profileImageUrl,
            bio = credentials.bio,
            followersCount = 125,
            followingCount = 98,
            postsCount = 4
        )
    }
}
