package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.domain.usecase.GetFeedPostsUseCase
import com.adr.instaapp.domain.usecase.LikePostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FeedViewModel(
    private val getFeedPostsUseCase: GetFeedPostsUseCase,
    private val likePostUseCase: LikePostUseCase
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

    fun refresh() {
        loadFeedPosts()
    }

    data class FeedUiState(
        val isLoading: Boolean = false,
        val posts: List<com.adr.instaapp.domain.model.Post> = emptyList(),
        val error: String? = null
    )
}
