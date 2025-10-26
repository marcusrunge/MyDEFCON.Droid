package com.marcusrunge.mydefcon.di

import android.content.Context
import com.marcusrunge.mydefcon.utils.LiveDataManager
import com.marcusrunge.mydefcon.utils.LiveDataManagerUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LiveDataManagerModule {
    @Provides
    @Singleton
    fun provideLiveDataManager(@ApplicationContext context: Context?): LiveDataManager =
        LiveDataManagerUtil.create(context)
}