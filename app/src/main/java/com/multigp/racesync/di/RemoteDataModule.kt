package com.multigp.racesync.di

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.repository.dataSource.ChaptersDataSource
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.data.repository.dataSource.RaceDataSource
import com.multigp.racesync.data.repository.dataSourceImpl.ChaptersDataSourceImpl
import com.multigp.racesync.data.repository.dataSourceImpl.OnboardingDataSourceImpl
import com.multigp.racesync.data.repository.dataSourceImpl.RaceDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {
    @Provides
    fun provideLoginRemoteDataSource(raceSyncApi: RaceSyncApi): OnboardingDataSource =
        OnboardingDataSourceImpl(raceSyncApi)


    @Provides
    fun provideChaptersRemoteDataSource(raceSyncApi: RaceSyncApi): ChaptersDataSource =
        ChaptersDataSourceImpl(raceSyncApi)

    @Provides
    fun provideRacesRemoteDataSource(raceSyncApi: RaceSyncApi): RaceDataSource =
        RaceDataSourceImpl(raceSyncApi)
}