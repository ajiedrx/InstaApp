package com.adr.instaapp.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val author: User,
    val content: String,
    val timestamp: Long,
    val level: Int = 0, // 0 for top-level, 1 for replies
    val parentId: String? = null, // Only set for replies
    val replies: List<Comment> = emptyList(),
    val isCurrentUserComment: Boolean = false
)
