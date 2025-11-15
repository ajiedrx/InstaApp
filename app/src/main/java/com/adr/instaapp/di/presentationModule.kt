package com.adr.instaapp.di

import com.adr.instaapp.presentation.viewmodel.FeedViewModel
import com.adr.instaapp.presentation.viewmodel.LoginViewModel
import com.adr.instaapp.presentation.viewmodel.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { FeedViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    
    // Presentation layer dependencies will be added here
    // Additional ViewModels, UI components, etc.
}
