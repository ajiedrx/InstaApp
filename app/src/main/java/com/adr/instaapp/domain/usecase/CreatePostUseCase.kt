package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.repository.PostRepository

data class CreatePostParams(
    val caption: String,
    val imageUrl: String
)

class CreatePostUseCase(
    private val postRepository: PostRepository
) : UseCase<CreatePostParams, Post>() {

    override suspend operator fun invoke(parameters: CreatePostParams): Result<Post> {
        return postRepository.createPost(
            caption = parameters.caption,
            imageUrl = parameters.imageUrl
        )
    }
}
