package com.marcusrunge.mydefcon.di

import android.content.Context
import com.marcusrunge.mydefcon.communication.CommunicationFactoryImpl
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommunicationModule {
    @Provides
    @Singleton
    fun provideData(@ApplicationContext context: Context?): Communication =
        CommunicationFactoryImpl.create(context)
}