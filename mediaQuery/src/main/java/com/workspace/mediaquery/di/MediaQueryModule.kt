package com.workspace.mediaquery.di

import android.content.ContentResolver
import android.content.Context
import com.workspace.mediaquery.MediaQuery
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MediaQueryModule {

    @Provides
    fun providesContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    @Provides
    fun providesMediaQuery(contentResolver: ContentResolver): MediaQuery {
        return MediaQuery(contentResolver)
    }

}