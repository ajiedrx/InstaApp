package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.data.datasource.DummyDataSource
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.usecase.DeletePostUseCase
import com.adr.instaapp.domain.usecase.GetCommentsByPostIdUseCase
import com.adr.instaapp.domain.usecase.GetCurrentUserUseCase
import com.adr.instaapp.domain.usecase.UpdatePostLikeParams
import com.adr.instaapp.domain.usecase.UpdatePostLikeUseCase
import com.adr.instaapp.presentation.screen.PostDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updatePostLikeUseCase: UpdatePostLikeUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val getCommentsByPostIdUseCase: GetCommentsByPostIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val currentUser = getCurrentUserUseCase()

                val post = findPostById(postId)

                if (post != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        post = post,
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete post"
                )
            }
        }
    }

    private fun findPostById(postId: String): Post? {
        return try {
            val dataSource = DummyDataSource()

            val feedPosts = dataSource.getFeedPosts()
            feedPosts.find { it.id == postId }
                ?: dataSource.getUserPosts().find { it.id == postId }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
