package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.NoParametersFlowUseCase
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetFeedPostsUseCase(
    private val postRepository: PostRepository
) : NoParametersFlowUseCase<List<Post>>() {

    override operator fun invoke(): Flow<List<Post>> {
        return postRepository.getFeedPosts()
    }
}
