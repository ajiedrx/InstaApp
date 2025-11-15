package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.repository.CommentRepository
import kotlinx.coroutines.flow.first

class GetCommentsByPostIdUseCase(
    private val commentRepository: CommentRepository
) : UseCase<String, List<Comment>>() {

    override suspend fun invoke(parameters: String): Result<List<Comment>> {
        return try {
            val comments = commentRepository.getCommentsForPost(parameters).first()
            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
