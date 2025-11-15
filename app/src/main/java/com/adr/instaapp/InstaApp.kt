package com.adr.instaapp

import android.app.Application
import com.adr.instaapp.di.appModule
import com.adr.instaapp.di.dataModule
import com.adr.instaapp.di.domainModule
import com.adr.instaapp.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class InstaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@InstaApp)
            modules(
                appModule,
                dataModule,
                domainModule,
                presentationModule
            )
        }
    }
}
