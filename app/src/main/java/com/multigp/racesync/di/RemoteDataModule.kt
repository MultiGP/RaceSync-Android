package com.multigp.racesync.di

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.data.repository.dataSourceImpl.OnboardingDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {
    @Provides
    fun provideMoviesRemoteDataSource(raceSyncApi: RaceSyncApi): OnboardingDataSource =
        OnboardingDataSourceImpl(raceSyncApi)
}