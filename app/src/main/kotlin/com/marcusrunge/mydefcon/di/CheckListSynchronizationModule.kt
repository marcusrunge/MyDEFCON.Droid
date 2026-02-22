package com.marcusrunge.mydefcon.di

import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.implementations.CheckListSynchronizationImpl
import com.marcusrunge.mydefcon.interfaces.CheckListSynchronization
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Dagger Hilt module for providing [CheckListSynchronization].
 *
 * This module is installed in the [SingletonComponent], meaning the provided instance
 * will have a singleton scope and live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object CheckListSynchronizationModule {
    /**
     * Provides a singleton instance of [CheckListSynchronization].
     *
     * This function creates an instance of [CheckListSynchronizationImpl] using its
     * factory `create` method, passing in the required dependencies.
     *
     * @param core The core component of the application.
     * @param data The data layer component.
     * @param firebase The Firebase integration component.
     * @return A singleton instance of [CheckListSynchronization].
     */
    @Provides
    @Singleton
    fun provideCheckListSynchronization(core: Core, data: Data, firebase: Firebase): CheckListSynchronization =
        CheckListSynchronizationImpl.create(core, data, firebase)
}
