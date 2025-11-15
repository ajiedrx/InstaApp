package com.adr.instaapp.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adr.instaapp.presentation.screen.LoginScreen
import com.adr.instaapp.presentation.screen.MainScreen
import com.adr.instaapp.presentation.screen.PostCreationScreen
import com.adr.instaapp.presentation.screen.PostDetailScreen
import com.adr.instaapp.presentation.screen.RegisterScreen
import com.adr.instaapp.presentation.viewmodel.AuthViewModel

@Composable
fun InstaAppNavigation(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isCheckingAuth by authViewModel.isCheckingAuth.collectAsState()

    LaunchedEffect(currentUser) {
        val currentRoute = navController.currentDestination?.route
        when {
            currentUser != null && (currentRoute == "login" || currentRoute == "register") -> {
                navController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            }

            currentUser == null && currentRoute != "login" && currentRoute != "register" && !isCheckingAuth -> {
                navController.navigate("login") {
                    popUpTo("main") { inclusive = true }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isCheckingAuth) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val startDestination = if (currentUser != null) "main" else "login"

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("login") {
                    LoginScreen(
                        onNavigateToMain = {
                            authViewModel.refreshAuthState()
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onNavigateToRegister = { navController.navigate("register") }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        onNavigateToMain = {
                            authViewModel.refreshAuthState()
                            navController.navigate("main") {
                                popUpTo("register") { inclusive = true }
                            }
                        },
                        onNavigateToLogin = { navController.navigateUp() }
                    )
                }

                composable("main") {
                    MainScreen(
                        onNavigateToPostCreation = { navController.navigate("post_creation") },
                        onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") },
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
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
    }
}
