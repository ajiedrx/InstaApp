package com.adr.instaapp.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.adr.instaapp.data.model.UserCredentials
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * SharedPreferences manager for persisting user authentication data
 */
class AuthPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "auth_preferences"
        private const val KEY_USERS = "registered_users"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
    }

    /**
     * Save a new user to SharedPreferences
     */
    fun saveUser(userCredentials: UserCredentials): Boolean {
        val currentUsers = getAllUsers().toMutableList()

        // Check if username or email already exists
        if (currentUsers.any { it.username == userCredentials.username || it.email == userCredentials.email }) {
            return false
        }

        currentUsers.add(userCredentials)
        saveAllUsers(currentUsers)
        return true
    }

    /**
     * Get all registered users
     */
    fun getAllUsers(): List<UserCredentials> {
        val usersJson = sharedPreferences.getString(KEY_USERS, null)
        return if (usersJson != null) {
            val type = object : TypeToken<List<UserCredentials>>() {}.type
            gson.fromJson(usersJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Save all users to SharedPreferences
     */
    private fun saveAllUsers(users: List<UserCredentials>) {
        val usersJson = gson.toJson(users)
        sharedPreferences.edit()
            .putString(KEY_USERS, usersJson)
            .apply()
    }

    /**
     * Find user by username
     */
    fun findUserByUsername(username: String): UserCredentials? {
        return getAllUsers().find { it.username == username }
    }

    /**
     * Check if username exists
     */
    fun isUsernameTaken(username: String): Boolean {
        return getAllUsers().any { it.username == username }
    }

    /**
     * Check if email exists
     */
    fun isEmailTaken(email: String): Boolean {
        return getAllUsers().any { it.email == email }
    }

    /**
     * Validate user credentials for login
     */
    fun validateCredentials(username: String, password: String): UserCredentials? {
        val user = findUserByUsername(username) ?: return null
        return if (user.password == password) user else null
    }

    /**
     * Save current logged-in user ID
     */
    fun saveCurrentUserId(userId: Long) {
        sharedPreferences.edit {
            putLong(KEY_CURRENT_USER_ID, userId)
        }
    }

    /**
     * Get current logged-in user ID
     */
    fun getCurrentUserId(): Long? {
        val userId = sharedPreferences.getLong(KEY_CURRENT_USER_ID, -1)
        return if (userId != -1L) userId else null
    }

    /**
     * Get current logged-in user credentials
     */
    fun getCurrentUser(): UserCredentials? {
        val currentUserId = getCurrentUserId() ?: return null
        return getAllUsers().find { it.id == currentUserId }
    }

    /**
     * Clear current user session (logout)
     */
    fun clearCurrentUser() {
        sharedPreferences.edit {
            remove(KEY_CURRENT_USER_ID)
        }
    }

}
