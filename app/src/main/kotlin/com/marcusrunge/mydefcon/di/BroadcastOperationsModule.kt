package com.marcusrunge.mydefcon.di

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.implementations.BroadcastOperationsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BroadcastOperationsModule {
    @Provides
    @Singleton
    fun provideBroadcastOperations(@ApplicationContext context: Context?): BroadcastOperations =
        BroadcastOperationsImpl(context)
}