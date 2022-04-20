package com.marcusrunge.mydefcon.di

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import com.marcusrunge.mydefcon.implementations.PreferencesOperationsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesOperationsModule {
    @Provides
    @Singleton
    fun providePreferencesOperations(@ApplicationContext context: Context?): PreferencesOperations =
        PreferencesOperationsImpl(context)
}