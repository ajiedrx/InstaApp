package com.adr.instaapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.adr.instaapp.presentation.viewmodel.FeedViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FeedScreen(
    onNavigateToPostDetail: (String) -> Unit = {}
) {
    val viewModel: FeedViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.error != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }

            uiState.posts.isEmpty() -> {
                Text(
                    text = "No posts yet",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> {
                var showComments by remember { mutableStateOf(false) }
                var selectedPostId by remember { mutableStateOf("") }

                LazyColumn {
                    items(uiState.posts) { post ->
                        PostItem(
                            post = post,
                            isOwnedByCurrentUser = viewModel.isPostOwnedByCurrentUser(post),
                            onLikeClick = { viewModel.likePost(post.id) },
                            onCommentClick = {
                                selectedPostId = post.id
                                showComments = true
                            },
                            onDeleteClick = { viewModel.deletePost(post.id) }
                        )
                    }
                }

                if (showComments) {
                    CommentBottomSheet(
                        postId = selectedPostId,
                        onDismiss = { showComments = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun PostItem(
    post: com.adr.instaapp.domain.model.Post,
    isOwnedByCurrentUser: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header with author info and options menu
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile picture
                AsyncImage(
                    model = post.author.profilePictureUrl,
                    contentDescription = "${post.author.username}'s profile picture",
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = post.author.username,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Options menu for user's own posts
            if (isOwnedByCurrentUser) {
                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete Post") },
                            onClick = {
                                onDeleteClick()
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Post Image
        AsyncImage(
            model = post.imageUrl,
            contentDescription = post.caption,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )

        // Engagement buttons
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onLikeClick
            ) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (post.isLiked) "Unlike" else "Like",
                    tint = if (post.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(24.dp)
                )
            }

            Text(
                text = "${post.likeCount} likes",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = onCommentClick
            ) {
                Icon(
                    imageVector = Icons.Filled.ModeComment,
                    contentDescription = "Comments",
                    modifier = Modifier.width(24.dp)
                )
            }

            Text(
                text = "${post.commentCount} comments",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Caption
        Text(
            text = post.caption,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
