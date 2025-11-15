package com.adr.instaapp.di

import com.adr.instaapp.domain.usecase.CreateCommentUseCase
import com.adr.instaapp.domain.usecase.CreatePostUseCase
import com.adr.instaapp.domain.usecase.DeleteCommentUseCase
import com.adr.instaapp.domain.usecase.DeletePostUseCase
import com.adr.instaapp.domain.usecase.GetCommentsUseCase
import com.adr.instaapp.domain.usecase.GetCurrentUserUseCase
import com.adr.instaapp.domain.usecase.GetFeedPostsUseCase
import com.adr.instaapp.domain.usecase.GetUserPostsUseCase
import com.adr.instaapp.domain.usecase.LikePostUseCase
import com.adr.instaapp.domain.usecase.LoginUseCase
import com.adr.instaapp.domain.usecase.UpdateCommentUseCase
import com.adr.instaapp.domain.usecase.UpdatePostUseCase
import org.koin.dsl.module

val domainModule = module {

    // Repository interfaces will be provided by data module

    // Use Cases
    single { GetFeedPostsUseCase(get()) }
    single { LoginUseCase(get()) }
    single { GetCurrentUserUseCase(get()) }
    single { LikePostUseCase(get()) }
    single { GetUserPostsUseCase(get()) }
    single { GetCommentsUseCase(get()) }
    single { CreateCommentUseCase(get()) }
    single { DeleteCommentUseCase(get()) }
    single { UpdateCommentUseCase(get()) }
    single { CreatePostUseCase(get()) }
    single { UpdatePostUseCase(get()) }
    single { DeletePostUseCase(get()) }
}
