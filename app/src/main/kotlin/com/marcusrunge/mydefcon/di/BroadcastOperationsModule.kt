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

/**
 * A Dagger Hilt module for providing broadcast-related dependencies.
 * This module is installed in the [SingletonComponent], meaning the provided dependencies
 * will have a singleton scope and live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object BroadcastOperationsModule {
    /**
     * Provides a singleton instance of [BroadcastOperations].
     *
     * @param context The application context, injected by Hilt.
     * @return A singleton instance of [BroadcastOperationsImpl].
     */
    @Provides
    @Singleton
    fun provideBroadcastOperations(@ApplicationContext context: Context?): BroadcastOperations =
        BroadcastOperationsImpl(context)
}
