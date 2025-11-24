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

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebase(@ApplicationContext context: Context?,  core: Core?): Firebase =
        FirebaseFactoryImpl.create(context, core)
}