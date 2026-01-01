package com.marcusrunge.mydefcon.di

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.FirebaseFactoryImpl
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Dagger Hilt module for providing the Firebase dependency.
 * This module is installed in the [SingletonComponent], meaning the provided [Firebase]
 * instance will have a singleton scope and live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    /**
     * Provides a singleton instance of the [Firebase] interface.
     *
     * This method creates the [Firebase] instance using [FirebaseFactoryImpl], which requires
     * the application context and the [Core] layer as dependencies.
     *
     * @param context The application context, injected by Hilt.
     * @param core The core business logic layer, injected by Hilt.
     * @return A singleton instance of [Firebase].
     */
    @Provides
    @Singleton
    fun provideFirebase(@ApplicationContext context: Context?, core: Core?): Firebase =
        FirebaseFactoryImpl.create(context, core)
}
