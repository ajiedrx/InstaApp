package com.adr.instaapp.di

import com.adr.instaapp.data.datasource.DummyDataSource
import com.adr.instaapp.data.repository.CommentRepositoryImpl
import com.adr.instaapp.data.repository.PostRepositoryImpl
import com.adr.instaapp.data.repository.UserRepositoryImpl
import com.adr.instaapp.domain.repository.CommentRepository
import com.adr.instaapp.domain.repository.PostRepository
import com.adr.instaapp.domain.repository.UserRepository
import org.koin.dsl.module

val dataModule = module {

    // Data Source
    single<DummyDataSource> { DummyDataSource() }

    // Repository Implementations
    single<PostRepository> { PostRepositoryImpl(get()) }
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(dataSource = get(), context = get()) }
}
