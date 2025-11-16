package com.adr.instaapp.data.datasource

import com.adr.instaapp.domain.model.Comment
import com.adr.instaapp.domain.model.Post
import com.adr.instaapp.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DummyDataSourceTest {

    private lateinit var dataSource: DummyDataSource
    private lateinit var testUser: User
    private lateinit var testPost: Post

    @Before
    fun setUp() {
        dataSource = DummyDataSource()
        testUser = dataSource.getCurrentUser()

        // Get a test post from the data source
        testPost = dataSource.getUserPosts().first()
    }

    @Test
    fun `initial comment count matches actual comments`() {
        // Get the post and its comments
        val post = dataSource.getPostById(testPost.id)!!
        val comments = dataSource.getCommentsForPost(testPost.id)

        // Count total comments including replies
        var totalComments = comments.size
        comments.forEach { comment ->
            totalComments += comment.replies.size
        }

        // Verify that the comment count matches the actual number of comments
        assertEquals(
            "Post comment count should match actual comment count",
            totalComments,
            post.commentCount
        )
    }

    @Test
    fun `adding a comment updates the post comment count`() {
        // Get initial state
        val postBefore = dataSource.getPostById(testPost.id)!!
        val initialCommentCount = postBefore.commentCount
        val initialComments = dataSource.getCommentsForPost(testPost.id)

        // Add a new comment
        val newComment = Comment(
            id = "test_comment_1",
            postId = testPost.id,
            author = testUser,
            content = "This is a test comment",
            timestamp = System.currentTimeMillis(),
            level = 0,
            isCurrentUserComment = true
        )

        dataSource.addComment(newComment)

        // Verify the comment was added
        val commentsAfter = dataSource.getCommentsForPost(testPost.id)
        assertTrue("Comment should be added", commentsAfter.any { it.id == "test_comment_1" })

        // Verify the comment count was updated
        val postAfter = dataSource.getPostById(testPost.id)!!
        assertEquals(
            "Comment count should increase by 1",
            initialCommentCount + 1,
            postAfter.commentCount
        )

        // Verify the count matches actual comments
        var totalComments = commentsAfter.size
        commentsAfter.forEach { comment ->
            totalComments += comment.replies.size
        }
        assertEquals(
            "Post comment count should match actual comment count",
            totalComments,
            postAfter.commentCount
        )
    }

    @Test
    fun `deleting a comment updates the post comment count`() {
        // First, get a post with comments
        val post = dataSource.getPostById(testPost.id)!!
        val comments = dataSource.getCommentsForPost(testPost.id)

        if (comments.isEmpty()) {
            // Add a comment first if there are none
            val newComment = Comment(
                id = "test_comment_delete",
                postId = testPost.id,
                author = testUser,
                content = "Comment to delete",
                timestamp = System.currentTimeMillis(),
                level = 0,
                isCurrentUserComment = true
            )
            dataSource.addComment(newComment)
        }

        // Get state before deletion
        val postBefore = dataSource.getPostById(testPost.id)!!
        val initialCommentCount = postBefore.commentCount
        val commentsBefore = dataSource.getCommentsForPost(testPost.id)
        val commentToDelete = commentsBefore.first()

        // Delete the comment
        val deleteResult = dataSource.deleteComment(commentToDelete.id, testPost.id)
        assertTrue("Comment deletion should succeed", deleteResult)

        // Verify the comment was deleted
        val commentsAfter = dataSource.getCommentsForPost(testPost.id)
        assertFalse("Comment should be deleted", commentsAfter.any { it.id == commentToDelete.id })

        // Verify the comment count was updated
        val postAfter = dataSource.getPostById(testPost.id)!!
        assertEquals(
            "Comment count should decrease by 1",
            initialCommentCount - 1,
            postAfter.commentCount
        )

        // Verify the count matches actual comments
        var totalComments = commentsAfter.size
        commentsAfter.forEach { comment ->
            totalComments += comment.replies.size
        }
        assertEquals(
            "Post comment count should match actual comment count",
            totalComments,
            postAfter.commentCount
        )
    }
}
