package com.workspace.core.player.di

import com.workspace.core.player.PlayerServiceConnection
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playerModule = module {
    single { PlayerServiceConnection(androidContext()) }
}
