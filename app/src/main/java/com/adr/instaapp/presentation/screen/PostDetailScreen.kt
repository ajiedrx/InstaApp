package com.adr.instaapp.presentation.screen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.adr.instaapp.presentation.viewmodel.CommentViewModel
import org.koin.androidx.compose.koinViewModel

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: com.adr.instaapp.domain.model.Post? = null,
    val error: String? = null,
    val currentUser: com.adr.instaapp.domain.model.User? = null
)

sealed interface PostDetailEvent {
    data class OnLoadPost(val postId: String) : PostDetailEvent
    data class OnLikePost(val postId: String) : PostDetailEvent
    data class OnDeletePost(val postId: String) : PostDetailEvent
    data class OnCommentClick(val postId: String) : PostDetailEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    uiState: PostDetailUiState,
    onEvent: (PostDetailEvent) -> Unit,
    postId: String,
    onBackClick: () -> Unit,
    currentUser: com.adr.instaapp.domain.model.User? = null
) {
    val commentViewModel: CommentViewModel = koinViewModel()
    var showComments by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        onEvent(PostDetailEvent.OnLoadPost(postId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.post != null -> {
                    uiState.post?.let { post ->
                        LazyColumn {
                            item {
                                PostDetailContent(
                                    postDetail = post,
                                    isOwnedByCurrentUser = currentUser?.id == post.author.id,
                                    onLikeClick = { onEvent(PostDetailEvent.OnLikePost(post.id)) },
                                    onCommentClick = {
                                        showComments = true
                                        onEvent(PostDetailEvent.OnCommentClick(post.id))
                                    },
                                    onDeleteClick = { onEvent(PostDetailEvent.OnDeletePost(post.id)) }
                                )
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Post not found",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (showComments) {
                val post = uiState.post
                if (post != null) {
                    CommentBottomSheet(
                        viewModel = commentViewModel,
                        postId = post.id,
                        onDismiss = { showComments = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostDetailContent(
    postDetail: com.adr.instaapp.domain.model.Post,
    isOwnedByCurrentUser: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header with author info
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = postDetail.author.profilePictureUrl,
                contentDescription = "${postDetail.author.username}'s profile picture",
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = postDetail.author.username,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Just now",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Post Image
        AsyncImage(
            model = postDetail.imageUrl,
            contentDescription = postDetail.caption,
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
                    imageVector = if (postDetail.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (postDetail.isLiked) "Unlike" else "Like",
                    tint = if (postDetail.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(28.dp)
                )
            }

            Text(
                text = "${postDetail.likeCount} likes",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = onCommentClick
            ) {
                Icon(
                    imageVector = Icons.Filled.ModeComment,
                    contentDescription = "Comments",
                    modifier = Modifier.width(28.dp)
                )
            }

            Text(
                text = "${postDetail.commentCount} comments",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Caption
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = postDetail.author.username,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = postDetail.caption,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
