package com.pauloavelar.inventory

import android.app.Application
import com.pauloavelar.inventory.config.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

@Suppress("unused")
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(allModules)
        }
    }

    override fun onTerminate() {
        super.onTerminate()

        stopKoin()
    }

}
