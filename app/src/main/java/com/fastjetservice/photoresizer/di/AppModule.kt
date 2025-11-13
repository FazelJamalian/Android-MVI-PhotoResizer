package com.fastjetservice.photoresizer.di

import android.content.Context
import com.fastjetservice.photoresizer.data.FFmpegImageCompressor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCompressor(@ApplicationContext context: Context): FFmpegImageCompressor {
        return FFmpegImageCompressor(context)
    }
}