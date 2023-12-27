package com.marcusrunge.mydefcon.di

import android.content.Context
import com.marcusrunge.mydefcon.notifications.NotificationsFactoryImpl
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {
    @Provides
    @Singleton
    fun providePNotifications(@ApplicationContext context: Context?): Notifications =
        NotificationsFactoryImpl.create(context)
}