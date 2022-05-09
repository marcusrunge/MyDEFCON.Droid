package com.marcusrunge.mydefcon.di

import com.marcusrunge.mydefcon.core.CoreFactoryImpl
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.DatabaseOperations
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides
    @Singleton
    fun provideCore(preferencesOperations: PreferencesOperations, databaseOperations: DatabaseOperations): Core =
        CoreFactoryImpl.create(preferencesOperations, databaseOperations)
}