package com.marcusrunge.mydefcon.di

import com.marcusrunge.mydefcon.core.CoreFactoryImpl
import com.marcusrunge.mydefcon.core.Interfaces.Core
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides
    fun provideCore(): Core = CoreFactoryImpl.create()
}