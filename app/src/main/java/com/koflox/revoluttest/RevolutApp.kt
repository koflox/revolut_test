package com.koflox.revoluttest

import android.app.Application
import com.koflox.revoluttest.di.revolutModules
import com.koflox.revoluttest.util.Network
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RevolutApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKoin()
        Network.init(this)
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@RevolutApp)
            modules(revolutModules)
        }
    }

}