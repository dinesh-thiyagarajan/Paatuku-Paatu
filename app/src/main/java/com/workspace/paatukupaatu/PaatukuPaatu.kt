package com.workspace.paatukupaatu

import android.app.Application
import com.workspace.core.database.di.databaseModule
import com.workspace.core.player.di.playerModule
import com.workspace.mediaquery.di.mediaQueryModule
import com.workspace.paatukupaatu.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PaatukuPaatu : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@PaatukuPaatu)
            modules(
                mediaQueryModule,
                databaseModule,
                playerModule,
                appModule
            )
        }
    }
}