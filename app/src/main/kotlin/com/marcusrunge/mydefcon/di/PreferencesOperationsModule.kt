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

/**
 * A Dagger Hilt module for providing preferences-related dependencies.
 * This module is installed in the [SingletonComponent], meaning the provided dependencies
 * will have a singleton scope and live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesOperationsModule {
    /**
     * Provides a singleton instance of [PreferencesOperations].
     *
     * @param context The application context, injected by Hilt.
     * @return A singleton instance of [PreferencesOperationsImpl].
     */
    @Provides
    @Singleton
    fun providePreferencesOperations(@ApplicationContext context: Context?): PreferencesOperations =
        PreferencesOperationsImpl(context)
}
