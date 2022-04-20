package com.marcusrunge.mydefcon.di

import android.content.Context
import com.marcusrunge.mydefcon.data.DataFactoryImpl
import com.marcusrunge.mydefcon.data.interfaces.Data
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideData(@ApplicationContext context: Context?): Data = DataFactoryImpl.create(context)
}