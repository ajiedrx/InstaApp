package com.adr.instaapp.di

import com.adr.instaapp.domain.usecase.GetFeedPostsUseCase
import com.adr.instaapp.domain.usecase.LoginUseCase
import org.koin.dsl.module

val domainModule = module {

    // Repository interfaces will be provided by data module

    // Use Cases
    single { GetFeedPostsUseCase(get()) }
    single { LoginUseCase(get()) }

    // Additional use cases will be added as we implement them
}
