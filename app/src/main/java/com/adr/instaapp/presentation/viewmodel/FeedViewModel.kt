package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.domain.usecase.DeletePostUseCase
import com.adr.instaapp.domain.usecase.GetCurrentUserUseCase
import com.adr.instaapp.domain.usecase.GetFeedPostsUseCase
import com.adr.instaapp.domain.usecase.LikePostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FeedViewModel(
    private val getFeedPostsUseCase: GetFeedPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState

    init {
        loadFeedPosts()
    }

    private fun loadFeedPosts() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            getFeedPostsUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load feed"
                    )

                }
                .collect { posts ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        posts = posts,
                        error = null
                    )
                }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            likePostUseCase(postId)
                .onSuccess {
                    loadFeedPosts()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to like post"
                    )
                }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            deletePostUseCase(postId)
                .onSuccess {
                    loadFeedPosts()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to delete post"
                    )
                }
        }
    }

    fun isPostOwnedByCurrentUser(post: com.adr.instaapp.domain.model.Post): Boolean {
        return runBlocking {
            try {
                val currentUser = getCurrentUserUseCase()
                currentUser?.id == post.author.id
            } catch (e: Exception) {
                false
            }
        }
    }

    fun refresh() {
        loadFeedPosts()
    }

    data class FeedUiState(
        val isLoading: Boolean = false,
        val posts: List<com.adr.instaapp.domain.model.Post> = emptyList(),
        val error: String? = null
    )
}
