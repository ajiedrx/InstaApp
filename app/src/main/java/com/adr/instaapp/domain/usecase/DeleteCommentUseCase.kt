package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.repository.CommentRepository

class DeleteCommentUseCase(
    private val commentRepository: CommentRepository
) : UseCase<String, Unit>() {

    override suspend operator fun invoke(parameters: String): Result<Unit> {
        return commentRepository.deleteComment(parameters)
    }
}
