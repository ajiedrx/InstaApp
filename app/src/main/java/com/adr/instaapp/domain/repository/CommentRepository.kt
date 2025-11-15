package com.adr.instaapp.domain.repository

import com.adr.instaapp.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getCommentsForPost(postId: String): Flow<List<Comment>>
    suspend fun createComment(
        postId: String,
        content: String,
        parentId: String? = null
    ): Result<Comment>

    suspend fun deleteComment(commentId: String): Result<Unit>
    suspend fun updateComment(commentId: String, content: String): Result<Comment>
}
