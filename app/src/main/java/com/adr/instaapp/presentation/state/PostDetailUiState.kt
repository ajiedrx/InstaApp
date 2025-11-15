package com.adr.instaapp.presentation.state

import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.model.User

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val currentUser: User? = null,
    val error: String? = null
)
