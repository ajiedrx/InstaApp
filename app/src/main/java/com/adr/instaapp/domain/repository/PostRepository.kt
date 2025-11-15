package com.adr.instaapp.domain.repository

import com.adr.instaapp.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getFeedPosts(): Flow<List<Post>>
    fun getUserPosts(userId: String): Flow<List<Post>>
    suspend fun likePost(postId: String): Result<Unit>
    suspend fun unlikePost(postId: String): Result<Unit>
    suspend fun updatePostLike(postId: String, isLiked: Boolean, increment: Int): Result<Post>
    suspend fun createPost(imageUrl: String, caption: String): Result<Post>
    suspend fun updatePost(postId: String, caption: String): Result<Post>
    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun getPostById(postId: String): Result<Post>
}
