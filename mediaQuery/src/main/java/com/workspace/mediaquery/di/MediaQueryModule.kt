package com.workspace.mediaquery.di

import com.workspace.mediaquery.MediaQuery
import com.workspace.mediaquery.domain.GetAudioFilesUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaQueryModule = module {
    single { MediaQuery(androidContext().contentResolver) }
    factory { GetAudioFilesUseCase(get()) }
}