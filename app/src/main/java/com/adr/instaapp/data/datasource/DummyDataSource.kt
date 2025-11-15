package com.adr.instaapp.data.datasource

import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.model.User
import kotlinx.coroutines.delay
import kotlin.random.Random

class DummyDataSource {

    // Current user (logged-in user)
    private val currentUser = User(
        id = "user_1",
        username = "johndoe",
        bio = "Photography enthusiast | Travel lover | Capturing moments 📸",
        profilePictureUrl = "https://picsum.photos/seed/user1/200/200",
        followersCount = 1250,
        followingCount = 342,
        postsCount = 4
    )

    // Other users for feed
    private val otherUsers = listOf(
        User(
            id = "user_2",
            username = "travel_girl",
            bio = "Exploring the world one city at a time ✈️",
            profilePictureUrl = "https://picsum.photos/seed/user2/200/200",
            followersCount = 3420,
            followingCount = 189,
            postsCount = 156
        ),
        User(
            id = "user_3",
            username = "foodie_life",
            bio = "🍕 Food blogger | Recipe developer | Eating my way through life",
            profilePictureUrl = "https://picsum.photos/seed/user3/200/200",
            followersCount = 5678,
            followingCount = 445,
            postsCount = 234
        ),
        User(
            id = "user_4",
            username = "fitness_motivation",
            bio = "💪 Fitness coach | Healthy lifestyle | Daily workouts",
            profilePictureUrl = "https://picsum.photos/seed/user4/200/200",
            followersCount = 8912,
            followingCount = 234,
            postsCount = 189
        ),
        User(
            id = "user_5",
            username = "art_creative",
            bio = "🎨 Digital artist | Creative soul | Color enthusiast",
            profilePictureUrl = "https://picsum.photos/seed/user5/200/200",
            followersCount = 2345,
            followingCount = 567,
            postsCount = 98
        )
    )

    // User's posts (4 posts for logged-in user)
    private val userPosts = mutableListOf<Post>(
        Post(
            id = "post_1",
            author = currentUser,
            imageUrl = "https://picsum.photos/seed/post1/400/400",
            caption = "Beautiful sunset at the beach 🌅",
            likeCount = 45,
            commentCount = 12,
            timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
            isLiked = false,
            isCurrentUserPost = true
        ),
        Post(
            id = "post_2",
            author = currentUser,
            imageUrl = "https://picsum.photos/seed/post2/400/400",
            caption = "Coffee and books ☕📚 Perfect morning!",
            likeCount = 23,
            commentCount = 5,
            timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
            isLiked = false,
            isCurrentUserPost = true
        ),
        Post(
            id = "post_3",
            author = currentUser,
            imageUrl = "https://picsum.photos/seed/post3/400/400",
            caption = "Hiking adventure in the mountains 🏔️",
            likeCount = 67,
            commentCount = 8,
            timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
            isLiked = false,
            isCurrentUserPost = true
        ),
        Post(
            id = "post_4",
            author = currentUser,
            imageUrl = "https://picsum.photos/seed/post4/400/400",
            caption = "City lights never disappoint ✨",
            likeCount = 89,
            commentCount = 15,
            timestamp = System.currentTimeMillis() - 345600000, // 4 days ago
            isLiked = false,
            isCurrentUserPost = true
        )
    )

    // Feed posts (10 posts from other users)
    private val feedPosts = mutableListOf<Post>().apply {
        val captions = listOf(
            "Amazing street art 🎨",
            "Delicious homemade pasta 🍝",
            "Morning yoga session 🧘‍♀️",
            "Weekend vibes 🌴",
            "Art gallery visit 🖼️",
            "Healthy breakfast bowl 🥗",
            "Sunset photography 📸",
            "Coffee time ☕",
            "Nature walk 🌳",
            "Reading corner 📖"
        )

        for (i in 0 until 10) {
            val user = otherUsers[i % otherUsers.size]
            add(
                Post(
                    id = "feed_post_${i + 1}",
                    author = user,
                    imageUrl = "https://picsum.photos/seed/feedpost${i + 1}/400/400",
                    caption = captions[i],
                    likeCount = Random.nextInt(20, 500),
                    commentCount = Random.nextInt(5, 50),
                    timestamp = System.currentTimeMillis() - (i + 1) * 3600000, // i+1 hours ago
                    isLiked = Random.nextBoolean(),
                    isCurrentUserPost = false
                )
            )
        }
    }

    // Comments storage (session-only)
    private val comments = mutableMapOf<String, MutableList<Comment>>()

    // Simulated API delay
    suspend fun simulateNetworkDelay() {
        delay(Random.nextLong(1000, 2000)) // 1-2 seconds delay
    }

    // Getters
    fun getCurrentUser(): User = currentUser

    fun getOtherUsers(): List<User> = otherUsers

    fun getUserPosts(): List<Post> = userPosts.toList()

    fun getFeedPosts(): List<Post> = feedPosts.toList()

    fun getPostById(postId: String): Post? {
        return userPosts.find { it.id == postId } ?: feedPosts.find { it.id == postId }
    }

    // Comments operations
    fun getCommentsForPost(postId: String): List<Comment> {
        return comments[postId]?.toList() ?: emptyList()
    }

    fun getAllComments(): List<Comment> {
        return comments.values.flatten()
    }

    fun addComment(comment: Comment) {
        val postComments = comments.getOrPut(comment.postId) { mutableListOf() }
        postComments.add(comment)
    }

    fun deleteComment(commentId: String, postId: String): Boolean {
        val postComments = comments[postId] ?: return false
        return postComments.removeIf { it.id == commentId }
    }

    // Post operations (session-only)
    fun updatePostLike(postId: String, isLiked: Boolean, increment: Int): Boolean {
        val post = getPostById(postId) ?: return false

        val updatedPost = post.copy(
            isLiked = isLiked,
            likeCount = post.likeCount + increment
        )

        return when {
            userPosts.any { it.id == postId } -> {
                val index = userPosts.indexOfFirst { it.id == postId }
                userPosts[index] = updatedPost
                true
            }

            feedPosts.any { it.id == postId } -> {
                val index = feedPosts.indexOfFirst { it.id == postId }
                feedPosts[index] = updatedPost
                true
            }

            else -> false
        }
    }

    fun createPost(post: Post): Boolean {
        return userPosts.add(post)
    }

    fun updatePost(postId: String, caption: String): Boolean {
        val postIndex = userPosts.indexOfFirst { it.id == postId }
        if (postIndex == -1) return false

        userPosts[postIndex] = userPosts[postIndex].copy(caption = caption)
        return true
    }

    fun deletePost(postId: String): Boolean {
        return userPosts.removeIf { it.id == postId }
    }
}
