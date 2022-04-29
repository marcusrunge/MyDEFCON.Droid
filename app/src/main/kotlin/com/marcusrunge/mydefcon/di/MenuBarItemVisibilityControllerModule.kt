package com.marcusrunge.mydefcon.di

import com.marcusrunge.mydefcon.services.implementations.MenuBarItemVisibilityControllerImpl
import com.marcusrunge.mydefcon.services.interfaces.MenuBarItemVisibilityController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MenuBarItemVisibilityControllerModule {
    @Provides
    @Singleton
    fun provideMenuBarItemVisibilityController(): MenuBarItemVisibilityController =
        MenuBarItemVisibilityControllerImpl.create()
}