package com.adr.instaapp.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adr.instaapp.R
import com.adr.instaapp.presentation.viewmodel.FeedViewModel
import com.adr.instaapp.presentation.viewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    onNavigateToPostCreation: () -> Unit = {},
    onNavigateToPostDetail: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToPostCreation
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "feed",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("feed") {
                val feedViewModel: FeedViewModel = koinViewModel()
                val uiState by feedViewModel.uiState.collectAsState()
                
                FeedScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        when (event) {
                            is FeedEvent.OnRefresh -> feedViewModel.refresh()
                            is FeedEvent.OnLikePost -> feedViewModel.likePost(event.postId)
                            is FeedEvent.OnDeletePost -> feedViewModel.deletePost(event.postId)
                            is FeedEvent.OnCommentClick -> { /* Handled by CommentBottomSheet */
                            }
                        }
                    }
                )
            }
            composable("profile") {
                val profileViewModel: ProfileViewModel = koinViewModel()
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToPostDetail = onNavigateToPostDetail,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = "feed",
            icon = R.drawable.ic_home,
            label = "Feed"
        ),
        BottomNavItem(
            route = "profile",
            icon = R.drawable.ic_profile,
            label = "Profile"
        )
    )

    BottomAppBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: Int,
    val label: String
)
