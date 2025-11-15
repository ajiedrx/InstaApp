package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.repository.PostRepository

data class UpdatePostParams(
    val postId: String,
    val caption: String
)

class UpdatePostUseCase(
    private val postRepository: PostRepository
) : UseCase<UpdatePostParams, Post>() {

    override suspend operator fun invoke(parameters: UpdatePostParams): Result<Post> {
        return postRepository.updatePost(
            postId = parameters.postId,
            caption = parameters.caption
        )
    }
}
