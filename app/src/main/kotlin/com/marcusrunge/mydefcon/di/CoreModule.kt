package com.marcusrunge.mydefcon.di

import com.marcusrunge.mydefcon.core.CoreFactoryImpl
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object CoreModule {
    @Provides
    fun provideCore(preferencesOperations: PreferencesOperations): Core =
        CoreFactoryImpl.create(preferencesOperations)
}