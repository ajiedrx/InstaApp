package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.repository.PostRepository

class LikePostUseCase(
    private val postRepository: PostRepository
) : UseCase<String, Unit>() {

    override suspend fun invoke(parameters: String): Result<Unit> {
        // Get the current post to check its like status
        val postResult = postRepository.getPostById(parameters)
        if (postResult.isFailure) {
            return Result.failure(postResult.exceptionOrNull() ?: Exception("Post not found"))
        }

        val post = postResult.getOrNull()
        if (post == null) {
            return Result.failure(Exception("Post not found"))
        }

        return if (post.isLiked) {
            postRepository.unlikePost(parameters)
        } else {
            postRepository.likePost(parameters)
        }
    }
}
