package com.marcusrunge.mydefcon.di

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.notifications.NotificationsFactoryImpl
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing notification-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {

    /**
     * Provides a singleton instance of [Notifications].
     *
     * @param context The application context.
     * @param core The core component.
     * @return A singleton instance of [Notifications].
     */
    @Provides
    @Singleton
    fun provideNotifications(
        @ApplicationContext context: Context?,
        core: Core?
    ): Notifications = NotificationsFactoryImpl.create(context, core)
}
