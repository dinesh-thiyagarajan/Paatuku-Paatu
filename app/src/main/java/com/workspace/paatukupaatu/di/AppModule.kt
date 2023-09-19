package com.workspace.paatukupaatu.di

import com.workspace.mediaquery.MediaQuery
import com.workspace.paatukupaatu.domain.GetAudioFilesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideAudioFilesUseCase(mediaQuery: MediaQuery) =
        GetAudioFilesUseCase(mediaQuery = mediaQuery)

}