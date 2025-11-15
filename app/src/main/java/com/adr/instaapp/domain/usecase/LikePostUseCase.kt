package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.repository.PostRepository

class LikePostUseCase(
    private val postRepository: PostRepository
) : UseCase<String, Unit>() {

    override suspend fun invoke(parameters: String): Result<Unit> {
        return postRepository.likePost(parameters)
    }
}
