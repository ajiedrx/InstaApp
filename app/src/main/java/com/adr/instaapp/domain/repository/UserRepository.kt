package com.adr.instaapp.domain.repository

import com.adr.instaapp.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String, bio: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun updateProfile(bio: String): Result<User>
}
