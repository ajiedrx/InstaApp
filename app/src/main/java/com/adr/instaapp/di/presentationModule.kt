package com.adr.instaapp.di

import com.adr.instaapp.presentation.viewmodel.AuthViewModel
import com.adr.instaapp.presentation.viewmodel.CommentViewModel
import com.adr.instaapp.presentation.viewmodel.FeedViewModel
import com.adr.instaapp.presentation.viewmodel.LoginViewModel
import com.adr.instaapp.presentation.viewmodel.PostCreationViewModel
import com.adr.instaapp.presentation.viewmodel.PostDetailViewModel
import com.adr.instaapp.presentation.viewmodel.ProfileViewModel
import com.adr.instaapp.presentation.viewmodel.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    // ViewModels
    viewModel { AuthViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel {
        FeedViewModel(
            getFeedPostsUseCase = get(),
            likePostUseCase = get(),
            getCurrentUserUseCase = get(),
            deletePostUseCase = get()
        )
    }
    viewModel {
        ProfileViewModel(
            getCurrentUserUseCase = get(),
            getUserPostsUseCase = get()
        )
    }
    viewModel {
        CommentViewModel(
            getCommentsUseCase = get(),
            createCommentUseCase = get(),
            deleteCommentUseCase = get()
        )
    }
    viewModel { PostCreationViewModel(get()) }
    viewModel<PostDetailViewModel> {
        PostDetailViewModel(
            getCurrentUserUseCase = get(),
            updatePostLikeUseCase = get(),
            deletePostUseCase = get(),
            getCommentsByPostIdUseCase = get()
        )
    }
}
