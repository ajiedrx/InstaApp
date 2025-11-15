package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.FlowUseCase
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetUserPostsUseCase(
    private val postRepository: PostRepository
) : FlowUseCase<String, List<Post>>() {

    override operator fun invoke(parameters: String): Flow<List<Post>> {
        return postRepository.getUserPosts(parameters)
    }
}
