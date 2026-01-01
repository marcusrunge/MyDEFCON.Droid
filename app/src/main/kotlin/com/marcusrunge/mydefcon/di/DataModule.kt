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

/**
 * A Dagger Hilt module for providing the data layer dependency.
 * This module is installed in the [SingletonComponent], meaning the provided [Data]
 * instance will have a singleton scope and live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    /**
     * Provides a singleton instance of the [Data] interface.
     *
     * This method creates the [Data] instance using [DataFactoryImpl], which requires
     * the application context.
     *
     * @param context The application context, injected by Hilt.
     * @return A singleton instance of [Data].
     */
    @Provides
    @Singleton
    fun provideData(@ApplicationContext context: Context?): Data = DataFactoryImpl.create(context)
}
