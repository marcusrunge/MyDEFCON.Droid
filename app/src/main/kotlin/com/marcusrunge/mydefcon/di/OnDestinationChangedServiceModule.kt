package com.marcusrunge.mydefcon.di

import com.marcusrunge.mydefcon.services.implementations.OnDestinationChangedServiceImpl
import com.marcusrunge.mydefcon.services.interfaces.MenuBarItemVisibilityController
import com.marcusrunge.mydefcon.services.interfaces.OnDestinationChangedService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnDestinationChangedServiceModule {
    @Provides
    @Singleton
    fun provideOnDestinationChangedService(controller: MenuBarItemVisibilityController): OnDestinationChangedService =
        OnDestinationChangedServiceImpl.create(controller)
}