package com.multigp.racesync.di

import com.multigp.racesync.domain.repositories.RaceSyncRepository
import com.multigp.racesync.domain.useCase.LoginUseCase
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideMovieUseCases(raceSyncRepository: RaceSyncRepository) = RaceSyncUseCases(
        performLoginUseCase = LoginUseCase(raceSyncRepository)
    )
}