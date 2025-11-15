package com.adr.instaapp.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adr.instaapp.presentation.screen.LoginScreen
import com.adr.instaapp.presentation.screen.MainScreen
import com.adr.instaapp.presentation.screen.PostCreationScreen
import com.adr.instaapp.presentation.screen.PostDetailScreen

@Composable
fun InstaAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToMain = { navController.navigate("main") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Register Screen",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        composable("main") {
            MainScreen(
                onNavigateToPostCreation = { navController.navigate("post_creation") },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") }
            )
        }

        composable("post_creation") {
            PostCreationScreen(
                onPostCreated = { navController.navigateUp() },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable("post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                postId = postId,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
