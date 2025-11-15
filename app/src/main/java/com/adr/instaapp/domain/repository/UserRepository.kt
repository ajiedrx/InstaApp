package com.adr.instaapp.domain.repository

import com.adr.instaapp.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    fun getCurrentUserFlow(): kotlinx.coroutines.flow.SharedFlow<User?>
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String, bio: String): Result<User>
    suspend fun logout(): Result<Unit>
}
