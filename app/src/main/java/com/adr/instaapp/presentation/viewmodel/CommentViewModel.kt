package com.adr.instaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.usecase.CreateCommentParams
import com.adr.instaapp.domain.usecase.CreateCommentUseCase
import com.adr.instaapp.domain.usecase.DeleteCommentUseCase
import com.adr.instaapp.domain.usecase.GetCommentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommentViewModel(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentUiState())
    val uiState: StateFlow<CommentUiState> = _uiState.asStateFlow()

    fun loadComments(postId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                getCommentsUseCase(postId).collect { comments ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        comments = comments,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load comments"
                )
            }
        }
    }


    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isDeletingComment = true)

                val result = deleteCommentUseCase(commentId)

                result.fold(
                    onSuccess = {
                        val currentComments = _uiState.value.comments.toMutableList()
                        currentComments.removeAll { comment ->
                            comment.id == commentId ||
                                    comment.replies.any { reply -> reply.id == commentId }
                        }
                        _uiState.value = _uiState.value.copy(
                            isDeletingComment = false,
                            comments = currentComments,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isDeletingComment = false,
                            error = error.message ?: "Failed to delete comment"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeletingComment = false,
                    error = e.message ?: "Failed to delete comment"
                )
            }
        }
    }

    fun updateCommentContent(content: String) {
        _uiState.value = _uiState.value.copy(newCommentContent = content)
    }

    fun setReplyingComment(commentId: String, authorUsername: String) {
        _uiState.value = _uiState.value.copy(
            replyingToCommentId = commentId,
            replyingToUsername = authorUsername,
            newCommentContent = ""
        )
    }

    fun clearReplyState() {
        _uiState.value = _uiState.value.copy(
            replyingToCommentId = null,
            replyingToUsername = null,
            newCommentContent = ""
        )
    }

    fun createComment(postId: String, content: String) {
        if (content.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Comment cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isCreatingComment = true)

                val result = createCommentUseCase(
                    CreateCommentParams(
                        postId = postId,
                        content = content.trim(),
                        parentId = _uiState.value.replyingToCommentId
                    )
                )

                result.fold(
                    onSuccess = { newComment ->
                        _uiState.value = _uiState.value.copy(
                            isCreatingComment = false,
                            newCommentContent = "",
                            replyingToCommentId = null,
                            replyingToUsername = null,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isCreatingComment = false,
                            error = error.message ?: "Failed to create comment"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreatingComment = false,
                    error = e.message ?: "Failed to create comment"
                )
            }
        }
    }

    data class CommentUiState(
        val isLoading: Boolean = false,
        val comments: List<Comment> = emptyList(),
        val isCreatingComment: Boolean = false,
        val isDeletingComment: Boolean = false,
        val newCommentContent: String = "",
        val replyingToCommentId: String? = null,
        val replyingToUsername: String? = null,
        val error: String? = null
    )
}
