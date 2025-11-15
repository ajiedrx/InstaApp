package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.repository.PostRepository

data class UpdatePostLikeParams(
    val postId: String,
    val isLiked: Boolean,
    val increment: Int
)

class UpdatePostLikeUseCase(
    private val postRepository: PostRepository
) : UseCase<UpdatePostLikeParams, Post>() {

    override suspend fun invoke(parameters: UpdatePostLikeParams): Result<Post> {
        return try {
            val result = postRepository.updatePostLike(
                postId = parameters.postId,
                isLiked = parameters.isLiked,
                increment = parameters.increment
            )
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
