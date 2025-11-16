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
import com.adr.instaapp.presentation.screen.LoginEvent
import com.adr.instaapp.presentation.screen.LoginScreen
import com.adr.instaapp.presentation.screen.MainScreen
import com.adr.instaapp.presentation.screen.PostCreationEvent
import com.adr.instaapp.presentation.screen.PostCreationScreen
import com.adr.instaapp.presentation.screen.PostDetailEvent
import com.adr.instaapp.presentation.screen.PostDetailScreen
import com.adr.instaapp.presentation.screen.RegisterEvent
import com.adr.instaapp.presentation.screen.RegisterScreen
import com.adr.instaapp.presentation.viewmodel.AuthViewModel
import com.adr.instaapp.presentation.viewmodel.LoginViewModel
import com.adr.instaapp.presentation.viewmodel.PostCreationViewModel
import com.adr.instaapp.presentation.viewmodel.PostDetailViewModel
import com.adr.instaapp.presentation.viewmodel.RegisterViewModel
import org.koin.androidx.compose.koinViewModel

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
                    val loginViewModel: LoginViewModel = koinViewModel()
                    val uiState by loginViewModel.uiState.collectAsState()
                    val navigateToMain by loginViewModel.navigateToMain.collectAsState()

                    LaunchedEffect(navigateToMain) {
                        if (navigateToMain) {
                            loginViewModel.onNavigationHandled()
                            authViewModel.refreshAuthState()
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    LoginScreen(
                        uiState = uiState,
                        onEvent = { event ->
                            when (event) {
                                is LoginEvent.OnLoginClick -> loginViewModel.login(
                                    uiState.username,
                                    uiState.password
                                )

                                is LoginEvent.OnUsernameChange -> loginViewModel.updateUsername(
                                    event.username
                                )

                                is LoginEvent.OnPasswordChange -> loginViewModel.updatePassword(
                                    event.password
                                )

                                is LoginEvent.OnRegisterClick -> navController.navigate("register")
                            }
                        },
                        onNavigateToMain = { /* Handled by LaunchedEffect */ },
                        onNavigateToRegister = { navController.navigate("register") }
                    )
                }

                composable("register") {
                    val registerViewModel: RegisterViewModel = koinViewModel()
                    val uiState by registerViewModel.uiState.collectAsState()
                    val navigateToMain by registerViewModel.navigateToMain.collectAsState()

                    LaunchedEffect(navigateToMain) {
                        if (navigateToMain) {
                            registerViewModel.onNavigationHandled()
                            authViewModel.refreshAuthState()
                            navController.navigate("main") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    }

                    RegisterScreen(
                        uiState = uiState,
                        onEvent = { event ->
                            when (event) {
                                is RegisterEvent.OnRegisterClick -> registerViewModel.register(
                                    uiState.email,
                                    uiState.username,
                                    uiState.password,
                                    uiState.bio
                                )

                                is RegisterEvent.OnEmailChange -> registerViewModel.updateEmail(
                                    event.email
                                )

                                is RegisterEvent.OnUsernameChange -> registerViewModel.updateUsername(
                                    event.username
                                )

                                is RegisterEvent.OnPasswordChange -> registerViewModel.updatePassword(
                                    event.password
                                )

                                is RegisterEvent.OnConfirmPasswordChange -> registerViewModel.updateConfirmPassword(
                                    event.confirmPassword
                                )

                                is RegisterEvent.OnBioChange -> registerViewModel.updateBio(event.bio)
                                is RegisterEvent.OnLoginClick -> navController.navigateUp()
                            }
                        },
                        onNavigateToMain = { /* Handled by LaunchedEffect */ },
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
                    val postCreationViewModel: PostCreationViewModel = koinViewModel()
                    val uiState by postCreationViewModel.uiState.collectAsState()
                    
                    PostCreationScreen(
                        uiState = uiState,
                        onEvent = { event ->
                            when (event) {
                                is PostCreationEvent.OnCreatePost -> postCreationViewModel.createPost()
                                is PostCreationEvent.OnCaptionChange -> postCreationViewModel.updateCaption(
                                    event.caption
                                )

                                is PostCreationEvent.OnNavigateBack -> navController.navigateUp()
                            }
                        },
                        onPostCreated = { navController.navigateUp() },
                        onNavigateBack = { navController.navigateUp() }
                    )
                }

                composable("post_detail/{postId}") { backStackEntry ->
                    val postDetailViewModel: PostDetailViewModel = koinViewModel()
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    val uiState by postDetailViewModel.uiState.collectAsState()

                    PostDetailScreen(
                        uiState = uiState,
                        onEvent = { event ->
                            when (event) {
                                is PostDetailEvent.OnLoadPost -> postDetailViewModel.loadPost(event.postId)
                                is PostDetailEvent.OnLikePost -> postDetailViewModel.likePost(event.postId)
                                is PostDetailEvent.OnDeletePost -> postDetailViewModel.deletePost(
                                    event.postId
                                )

                                is PostDetailEvent.OnCommentClick -> { /* Handled by CommentBottomSheet */
                                }
                            }
                        },
                        postId = postId,
                        onBackClick = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
