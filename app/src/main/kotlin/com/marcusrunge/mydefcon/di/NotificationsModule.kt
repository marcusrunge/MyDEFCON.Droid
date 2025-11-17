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

@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {
    @Provides
    @Singleton
    fun provideNotifications(@ApplicationContext context: Context?, core: Core?): Notifications =
        NotificationsFactoryImpl.create(context, core)
}