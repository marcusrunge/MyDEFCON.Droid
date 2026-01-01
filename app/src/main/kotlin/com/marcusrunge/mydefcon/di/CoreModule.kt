package com.marcusrunge.mydefcon.di

import com.marcusrunge.mydefcon.core.CoreFactoryImpl
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Dagger Hilt module for providing the core business logic dependency.
 * This module is installed in the [SingletonComponent], meaning the provided [Core]
 * instance will have a singleton scope and live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    /**
     * Provides a singleton instance of the [Core] interface.
     *
     * This method creates the [Core] instance using [CoreFactoryImpl], which requires
     * [PreferencesOperations] and [BroadcastOperations] as dependencies. These dependencies
     * are provided by other Hilt modules ([PreferencesOperationsModule] and [BroadcastOperationsModule]).
     *
     * @param preferencesOperations The operations for handling shared preferences.
     * @param broadcastOperations The operations for handling system broadcasts.
     * @return A singleton instance of [Core].
     */
    @Provides
    @Singleton
    fun provideCore(
        preferencesOperations: PreferencesOperations,
        broadcastOperations: BroadcastOperations
    ): Core =
        CoreFactoryImpl.create(preferencesOperations, broadcastOperations)
}
