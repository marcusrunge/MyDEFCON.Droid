package com.marcusrunge.mydefcon.di

import com.marcusrunge.mydefcon.core.interfaces.DatabaseOperations
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.implementations.DatabaseOperationsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseOperationsModule {
    @Provides
    @Singleton
    fun provideDatabaseOperations(data: Data): DatabaseOperations {
        return DatabaseOperationsImpl(data)
    }
}