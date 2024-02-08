package com.multigp.racesync.di

import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.data.repository.LoginRepositoryImpl
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.repositories.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideLoginRepository(
        onboardingDataSource: OnboardingDataSource,
        dataStore: DataStoreManager,
    ): LoginRepository = LoginRepositoryImpl(onboardingDataSource, dataStore)
}