package com.adr.instaapp.di

import com.adr.instaapp.presentation.viewmodel.CommentViewModel
import com.adr.instaapp.presentation.viewmodel.FeedViewModel
import com.adr.instaapp.presentation.viewmodel.LoginViewModel
import com.adr.instaapp.presentation.viewmodel.PostCreationViewModel
import com.adr.instaapp.presentation.viewmodel.PostDetailViewModel
import com.adr.instaapp.presentation.viewmodel.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    // ViewModels
    viewModel { LoginViewModel(get()) }
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
            getCommentsByPostIdUseCase = get(),
            createCommentUseCase = get(),
            updateCommentUseCase = get(),
            deleteCommentUseCase = get()
        )
    }
}
