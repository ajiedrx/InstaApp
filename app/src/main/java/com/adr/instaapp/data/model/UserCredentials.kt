package com.adr.instaapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class for storing user credentials in SharedPreferences
 */
data class UserCredentials(
    @SerializedName("id")
    val id: Long,

    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("bio")
    val bio: String,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String,

    @SerializedName("createdAt")
    val createdAt: Long
)
