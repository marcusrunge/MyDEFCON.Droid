package com.marcusrunge.mydefcon.di

import android.app.Application
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import com.marcusrunge.mydefcon.implementations.PreferencesOperationsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    fun providePreferencesOperations(
        application: Application
    ): PreferencesOperations = PreferencesOperationsImpl(application)
}