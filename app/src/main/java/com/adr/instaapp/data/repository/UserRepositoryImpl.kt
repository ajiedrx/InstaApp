package com.adr.instaapp.data.repository

import com.adr.instaapp.data.datasource.DummyDataSource
import com.adr.instaapp.domain.model.User
import com.adr.instaapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class UserRepositoryImpl(
    private val dataSource: DummyDataSource
) : UserRepository {

    private val _currentUserFlow = MutableSharedFlow<User?>(
        replay = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    private var currentUser: User? = null
    private var isLoggedIn = false

    init {
        _currentUserFlow.tryEmit(null)
    }

    override suspend fun getCurrentUser(): User? = currentUser

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            val user = dataSource.getCurrentUser()
            currentUser = user
            _currentUserFlow.tryEmit(user)
            isLoggedIn = true

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, password: String, bio: String): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            val user = dataSource.getCurrentUser()
            currentUser = user
            _currentUserFlow.tryEmit(user)
            isLoggedIn = true

            Result.success(user)
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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(bio: String): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            if (!isLoggedIn || currentUser == null) {
                return Result.failure(Exception("User not logged in"))
            }

            // In a real app, we'd update user's bio in the database
            // For this dummy implementation, we'll just return the current user
            val user = dataSource.getCurrentUser()
            // Note: In a real implementation, we'd update the user bio
            // For now, we'll just return the existing user

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
