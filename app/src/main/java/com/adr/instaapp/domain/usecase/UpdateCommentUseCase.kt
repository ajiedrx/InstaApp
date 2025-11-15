package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.repository.CommentRepository

data class UpdateCommentParams(
    val commentId: String,
    val content: String
)

class UpdateCommentUseCase(
    private val commentRepository: CommentRepository
) : UseCase<UpdateCommentParams, Comment>() {

    override suspend operator fun invoke(parameters: UpdateCommentParams): Result<Comment> {
        return commentRepository.updateComment(
            commentId = parameters.commentId,
            content = parameters.content
        )
    }
}
