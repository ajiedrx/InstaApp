package com.adr.instaapp.data.repository

import com.adr.instaapp.data.datasource.DummyDataSource
import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CommentRepositoryImpl(
    private val dataSource: DummyDataSource
) : CommentRepository {

    private val _commentsFlow = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())

    init {
        refreshComments()
    }

    override fun getCommentsForPost(postId: String): Flow<List<Comment>> {
        return _commentsFlow.map { commentsMap ->
            commentsMap[postId] ?: emptyList()
        }
    }

    override suspend fun createComment(
        postId: String,
        content: String,
        parentId: String?
    ): Result<Comment> {
        return try {
            dataSource.simulateNetworkDelay()
            val currentUser = dataSource.getCurrentUser()

            val level = if (parentId != null) 1 else 0

            val newComment = Comment(
                id = UUID.randomUUID().toString(),
                postId = postId,
                author = currentUser,
                content = content,
                timestamp = System.currentTimeMillis(),
                level = level,
                parentId = parentId,
                replies = emptyList(),
                isCurrentUserComment = true
            )

            dataSource.addComment(newComment)
            refreshComments()
            Result.success(newComment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            dataSource.simulateNetworkDelay()

            val commentsMap = _commentsFlow.value
            val commentToDelete = commentsMap.values.flatten()
                .find { it.id == commentId && it.isCurrentUserComment }

            if (commentToDelete == null) {
                return Result.failure(Exception("Comment not found or no permission to delete"))
            }

            val success = dataSource.deleteComment(commentId, commentToDelete.postId)
            if (success) {
                refreshComments()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateComment(commentId: String, content: String): Result<Comment> {
        return try {
            dataSource.simulateNetworkDelay()

            val commentsMap = _commentsFlow.value
            val commentToUpdate = commentsMap.values.flatten()
                .find { it.id == commentId && it.isCurrentUserComment }

            if (commentToUpdate == null) {
                return Result.failure(Exception("Comment not found or no permission to edit"))
            }

            val updatedComment = commentToUpdate.copy(
                content = content,
                timestamp = System.currentTimeMillis()
            )

            dataSource.deleteComment(commentId, commentToUpdate.postId)
            dataSource.addComment(updatedComment)
            refreshComments()

            Result.success(updatedComment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun refreshComments() {
        // Get all posts from data source and build comments map
        val feedPosts = dataSource.getFeedPosts()
        val userPosts = dataSource.getUserPosts()
        val allPosts = feedPosts + userPosts

        val commentsMap = mutableMapOf<String, List<Comment>>()

        allPosts.forEach { post ->
            val comments = dataSource.getCommentsForPost(post.id)
            // Organize comments with replies
            val topLevelComments = comments.filter { it.level == 0 }
            val replies = comments.filter { it.level == 1 }

            val organizedComments = topLevelComments.map { topComment ->
                val commentReplies = replies.filter { it.parentId == topComment.id }
                topComment.copy(replies = commentReplies)
            }

            commentsMap[post.id] = organizedComments
        }

        _commentsFlow.value = commentsMap
    }
}
