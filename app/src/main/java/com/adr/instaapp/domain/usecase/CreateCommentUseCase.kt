package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.repository.CommentRepository

data class CreateCommentParams(
    val postId: String,
    val content: String,
    val parentId: String? = null
)

class CreateCommentUseCase(
    private val commentRepository: CommentRepository
) : UseCase<CreateCommentParams, Comment>() {

    override suspend operator fun invoke(parameters: CreateCommentParams): Result<Comment> {
        return commentRepository.createComment(
            postId = parameters.postId,
            content = parameters.content,
            parentId = parameters.parentId
        )
    }
}
