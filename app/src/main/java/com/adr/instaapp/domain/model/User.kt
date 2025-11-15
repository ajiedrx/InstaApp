package com.adr.instaapp.domain.model

data class User(
    val id: String,
    val username: String,
    val bio: String,
    val profilePictureUrl: String,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int
)
