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

    private val comments = mutableMapOf<String, MutableList<Comment>>()

    init {
        initializeSampleComments()
    }

    suspend fun simulateNetworkDelay() {
        delay(Random.nextLong(1000, 2000))
    }

    // Getters
    fun getCurrentUser(): User = currentUser

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
        updatePostCommentCount(comment.postId)
    }

    fun deleteComment(commentId: String, postId: String): Boolean {
        val postComments = comments[postId] ?: return false
        val removed = postComments.removeIf { it.id == commentId }
        if (removed) {
            updatePostCommentCount(postId)
        }
        return removed
    }

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

    private fun updatePostCommentCount(postId: String) {
        val actualCommentCount = getActualCommentCount(postId)

        // Update comment count in userPosts
        val userPostIndex = userPosts.indexOfFirst { it.id == postId }
        if (userPostIndex != -1) {
            userPosts[userPostIndex] =
                userPosts[userPostIndex].copy(commentCount = actualCommentCount)
        }

        // Update comment count in feedPosts
        val feedPostIndex = feedPosts.indexOfFirst { it.id == postId }
        if (feedPostIndex != -1) {
            feedPosts[feedPostIndex] =
                feedPosts[feedPostIndex].copy(commentCount = actualCommentCount)
        }
    }

    private fun getActualCommentCount(postId: String): Int {
        val postComments = comments[postId] ?: return 0
        var totalCount = postComments.size

        // Count replies as separate comments
        postComments.forEach { comment ->
            totalCount += comment.replies.size
        }

        return totalCount
    }

    private fun initializeSampleComments() {
        val allPosts = userPosts + feedPosts

        allPosts.forEach { post ->
            val postComments = mutableListOf<Comment>()
            // Create a reasonable number of sample comments (2-4 per post)
            val numberOfComments = Random.nextInt(2, 5)

            for (i in 0 until numberOfComments) {
                val isReply =
                    i > 1 && Random.nextBoolean() // Make later comments potentially replies
                val author = if (isReply) currentUser else otherUsers.random()
                val parentComment = if (isReply) postComments.firstOrNull() else null

                val comment = Comment(
                    id = "comment_${post.id}_$i",
                    postId = post.id,
                    author = author,
                    content = when (i) {
                        0 -> "Amazing post! 📸"
                        1 -> "Love this! ❤️"
                        2 -> "Great shot! 👏"
                        3 -> "Where was this taken? 📍"
                        else -> "Thanks for sharing! ✨"
                    },
                    timestamp = post.timestamp - (i * 3600000L),
                    level = if (isReply) 1 else 0,
                    parentId = parentComment?.id,
                    replies = if (!isReply && i == 0 && numberOfComments > 2) {
                        // Add a reply to the first comment if there are enough comments
                        listOf(
                            Comment(
                                id = "reply_${post.id}_0_1",
                                postId = post.id,
                                author = otherUsers.random(),
                                content = "I agree! 👍",
                                timestamp = post.timestamp - 1800000L,
                                level = 1,
                                parentId = "comment_${post.id}_0",
                                replies = emptyList(),
                                isCurrentUserComment = false
                            )
                        )
                    } else emptyList(),
                    isCurrentUserComment = author.id == currentUser.id
                )

                postComments.add(comment)
            }

            comments[post.id] = postComments

            // Update the post's comment count to match the actual number of comments created
            updatePostCommentCount(post.id)
        }
    }
}
