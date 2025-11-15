package com.adr.instaapp.data.repository

import com.adr.instaapp.data.datasource.DummyDataSource
import com.adr.instaapp.domain.model.User
import com.adr.instaapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class UserRepositoryImpl(
    private val dataSource: DummyDataSource
) : UserRepository {

    private val _currentUserFlow = MutableStateFlow<User?>(null)
    private var isLoggedIn = false

    init {
        _currentUserFlow.value = null
    }

    override fun getCurrentUser(): Flow<User?> = _currentUserFlow

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            val currentUser = dataSource.getCurrentUser()
            _currentUserFlow.value = currentUser
            isLoggedIn = true

            Result.success(currentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(username: String, password: String, bio: String): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            val currentUser = dataSource.getCurrentUser()

            _currentUserFlow.value = currentUser
            isLoggedIn = true

            Result.success(currentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            dataSource.simulateNetworkDelay()
            _currentUserFlow.value = null
            isLoggedIn = false
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(bio: String): Result<User> {
        return try {
            dataSource.simulateNetworkDelay()

            if (!isLoggedIn || _currentUserFlow.value == null) {
                return Result.failure(Exception("User not logged in"))
            }

            // In a real app, we'd update the user's bio in the database
            // For this dummy implementation, we'll just return the current user
            val currentUser = dataSource.getCurrentUser()
            // Note: In a real implementation, we'd update the user bio
            // For now, we'll just return the existing user

            Result.success(currentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
