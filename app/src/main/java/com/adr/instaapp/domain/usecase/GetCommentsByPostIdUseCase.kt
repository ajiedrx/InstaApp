package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.repository.CommentRepository

class GetCommentsByPostIdUseCase(
    private val commentRepository: CommentRepository
) : UseCase<String, List<Comment>>() {

    override suspend fun invoke(parameters: String): Result<List<Comment>> {
        return try {
            // For now, return empty list since we're using a Flow in the actual implementation
            // In a real app, this would be a single call to get comments
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
