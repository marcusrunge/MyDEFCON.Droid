package com.marcusrunge.mydefcon.di

import androidx.navigation.NavController
import com.marcusrunge.mydefcon.services.implementations.OnDestinationChangedListenerImpl
import com.marcusrunge.mydefcon.services.interfaces.OnDestinationChangedService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnDestinationChangedListenerModule {
    @Provides
    @Singleton
    fun provideOnDestinationChangedListener(service: OnDestinationChangedService): NavController.OnDestinationChangedListener =
        OnDestinationChangedListenerImpl(service)
}