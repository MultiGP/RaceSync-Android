package com.multigp.racesync.di

import android.content.Context
import com.multigp.racesync.data.prefs.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) = DataStoreManager(context)
}