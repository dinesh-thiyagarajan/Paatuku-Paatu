package com.workspace.core.database.di

import androidx.room.Room
import com.workspace.core.database.AppDatabase
import com.workspace.core.database.repository.FavoriteRepository
import com.workspace.core.database.repository.ListeningHistoryRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "paatukupaatu.db"
        ).build()
    }

    single { get<AppDatabase>().favoriteDao() }
    single { get<AppDatabase>().listeningHistoryDao() }

    single { FavoriteRepository(get()) }
    single { ListeningHistoryRepository(get()) }
}
