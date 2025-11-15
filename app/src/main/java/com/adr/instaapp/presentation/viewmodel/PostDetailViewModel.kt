package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.data.datasource.DummyDataSource
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.usecase.CreateCommentUseCase
import com.adr.instaapp.domain.usecase.DeleteCommentUseCase
import com.adr.instaapp.domain.usecase.DeletePostUseCase
import com.adr.instaapp.domain.usecase.GetCommentsByPostIdUseCase
import com.adr.instaapp.domain.usecase.GetCurrentUserUseCase
import com.adr.instaapp.domain.usecase.UpdateCommentParams
import com.adr.instaapp.domain.usecase.UpdateCommentUseCase
import com.adr.instaapp.domain.usecase.UpdatePostLikeParams
import com.adr.instaapp.domain.usecase.UpdatePostLikeUseCase
import com.adr.instaapp.presentation.state.PostDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updatePostLikeUseCase: UpdatePostLikeUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val getCommentsByPostIdUseCase: GetCommentsByPostIdUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Get current user first
                val currentUser = getCurrentUserUseCase()

                // Find post in both user posts and feed posts
                val post = findPostById(postId)

                if (post != null) {
                    // Get comments for this post
                    val commentsResult = getCommentsByPostIdUseCase(postId)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        post = post,
                        comments = commentsResult.getOrNull() ?: emptyList(),
                        currentUser = currentUser,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Post not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                val currentPost = _uiState.value.post
                if (currentPost != null) {
                    val params = UpdatePostLikeParams(
                        postId = postId,
                        isLiked = !currentPost.isLiked,
                        increment = if (!currentPost.isLiked) 1 else -1
                    )
                    val updatedPostResult = updatePostLikeUseCase(params)

                    _uiState.value = _uiState.value.copy(post = updatedPostResult.getOrNull())
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to like post"
                )
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                deletePostUseCase(postId)
                // Navigate back would be handled by UI
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete post"
                )
            }
        }
    }

    fun createComment(postId: String, content: String) {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.currentUser
                if (currentUser != null) {
                    val params = com.adr.instaapp.domain.usecase.CreateCommentParams(
                        postId = postId,
                        content = content
                    )
                    createCommentUseCase(params)

                    // Refresh comments
                    loadPost(_uiState.value.post?.id ?: postId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to create comment"
                )
            }
        }
    }

    fun updateComment(commentId: String, content: String) {
        viewModelScope.launch {
            try {
                val params = UpdateCommentParams(
                    commentId = commentId,
                    content = content
                )
                updateCommentUseCase(params)
                // Refresh comments
                loadPost(_uiState.value.post?.id ?: return@launch)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update comment"
                )
            }
        }
    }

    fun deleteComment(commentId: String, postId: String) {
        viewModelScope.launch {
            try {
                deleteCommentUseCase.invoke(commentId)
                // Refresh comments
                loadPost(postId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete comment"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private suspend fun findPostById(postId: String): Post? {
        return try {
            // Try to get from data source directly
            // This is a simplified approach - in a real app, you'd have a GetPostByIdUseCase
            val dataSource = DummyDataSource()

            // Try to find in feed posts first
            val feedPosts = dataSource.getFeedPosts()
            feedPosts.find { it.id == postId }
            // Then try user posts
                ?: dataSource.getUserPosts().find { it.id == postId }
        } catch (e: Exception) {
            null
        }
    }

    fun isPostOwnedByCurrentUser(post: Post): Boolean {
        val currentUser = _uiState.value.currentUser
        return currentUser != null && post.author.id == currentUser.id
    }
}
