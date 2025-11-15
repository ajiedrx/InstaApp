package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.UseCase
import com.adr.instaapp.domain.repository.PostRepository

class DeletePostUseCase(
    private val postRepository: PostRepository
) : UseCase<String, Unit>() {

    override suspend operator fun invoke(parameters: String): Result<Unit> {
        return postRepository.deletePost(parameters)
    }
}
