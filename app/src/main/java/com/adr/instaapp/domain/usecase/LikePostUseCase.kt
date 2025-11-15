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

        val post = postResult.getOrNull()!!

        // Toggle the like status
        return if (post.isLiked) {
            // Unlike the post
            postRepository.unlikePost(parameters)
        } else {
            // Like the post
            postRepository.likePost(parameters)
        }
    }
}
