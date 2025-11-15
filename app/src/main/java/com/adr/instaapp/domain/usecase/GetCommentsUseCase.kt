package com.adr.instaapp.domain.usecase

import com.adr.instaapp.domain.base.FlowUseCase
import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow

class GetCommentsUseCase(
    private val commentRepository: CommentRepository
) : FlowUseCase<String, List<Comment>>() {

    override operator fun invoke(parameters: String): Flow<List<Comment>> {
        return commentRepository.getCommentsForPost(parameters)
    }
}
