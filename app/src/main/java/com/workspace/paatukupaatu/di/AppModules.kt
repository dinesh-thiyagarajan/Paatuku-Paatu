package com.workspace.paatukupaatu.di

import com.workspace.feature.favorites.FavoritesViewModel
import com.workspace.feature.home.HomeViewModel
import com.workspace.feature.nowplaying.NowPlayingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        HomeViewModel(
            getAudioFilesUseCase = get(),
            playerServiceConnection = get(),
            favoriteRepository = get(),
            listeningHistoryRepository = get()
        )
    }

    viewModel {
        NowPlayingViewModel(
            playerServiceConnection = get(),
            favoriteRepository = get(),
            listeningHistoryRepository = get()
        )
    }

    viewModel {
        FavoritesViewModel(
            favoriteRepository = get(),
            getAudioFilesUseCase = get(),
            playerServiceConnection = get()
        )
    }
}
