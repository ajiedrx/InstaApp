package com.adr.instaapp.data.repository

import com.adr.instaapp.data.datasource.DummyDataSource
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class PostRepositoryImpl(
    private val dataSource: DummyDataSource
) : PostRepository {

    private val _feedPostsFlow = MutableStateFlow<List<Post>>(emptyList())
    private val _userPostsFlow = MutableStateFlow<List<Post>>(emptyList())

    init {
        // Initialize with dummy data
        refreshFeedPosts()
        refreshUserPosts()
    }

    override fun getFeedPosts(): Flow<List<Post>> = _feedPostsFlow

    override fun getUserPosts(userId: String): Flow<List<Post>> = _userPostsFlow

    override suspend fun likePost(postId: String): Result<Unit> {
        return try {
            dataSource.simulateNetworkDelay()
            val success = dataSource.updatePostLike(postId, true, 1)
            if (success) {
                refreshAllPosts()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikePost(postId: String): Result<Unit> {
        return try {
            dataSource.simulateNetworkDelay()
            val success = dataSource.updatePostLike(postId, false, -1)
            if (success) {
                refreshAllPosts()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPost(imageUrl: String, caption: String): Result<Post> {
        return try {
            dataSource.simulateNetworkDelay()
            val currentUser = dataSource.getCurrentUser()
            val newPost = Post(
                id = UUID.randomUUID().toString(),
                author = currentUser,
                imageUrl = imageUrl,
                caption = caption,
                likeCount = 0,
                commentCount = 0,
                timestamp = System.currentTimeMillis(),
                isLiked = false,
                isCurrentUserPost = true
            )

            val success = dataSource.createPost(newPost)
            if (success) {
                refreshUserPosts()
                Result.success(newPost)
            } else {
                Result.failure(Exception("Failed to create post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePost(postId: String, caption: String): Result<Post> {
        return try {
            dataSource.simulateNetworkDelay()
            val success = dataSource.updatePost(postId, caption)
            if (success) {
                refreshUserPosts()
                val post = dataSource.getPostById(postId)
                if (post != null) {
                    Result.success(post)
                } else {
                    Result.failure(Exception("Post not found after update"))
                }
            } else {
                Result.failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            dataSource.simulateNetworkDelay()
            val success = dataSource.deletePost(postId)
            if (success) {
                refreshUserPosts()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPostById(postId: String): Result<Post> {
        return try {
            dataSource.simulateNetworkDelay()
            val post = dataSource.getPostById(postId)
            if (post != null) {
                Result.success(post)
            } else {
                Result.failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun refreshFeedPosts() {
        _feedPostsFlow.value = dataSource.getFeedPosts()
    }

    private fun refreshUserPosts() {
        _userPostsFlow.value = dataSource.getUserPosts()
    }

    private fun refreshAllPosts() {
        refreshFeedPosts()
        refreshUserPosts()
    }
}
