package com.adr.instaapp.domain.model

data class Post(
    val id: String,
    val author: User,
    val imageUrl: String,
    val caption: String,
    val likeCount: Int,
    val commentCount: Int,
    val timestamp: Long,
    val isLiked: Boolean,
    val isCurrentUserPost: Boolean = false
)
