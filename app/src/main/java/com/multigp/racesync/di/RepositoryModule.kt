package com.multigp.racesync.di

import com.multigp.racesync.data.repository.RaceSyncRepositoryImpl
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.repositories.RaceSyncRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideMoviesRepository(
        onboardingDataSource: OnboardingDataSource
    ): RaceSyncRepository = RaceSyncRepositoryImpl(onboardingDataSource)
}