package com.multigp.racesync.di

import com.multigp.racesync.domain.repositories.LoginRepository
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
    fun provideMovieUseCases(loginRepository: LoginRepository) = RaceSyncUseCases(
        performLoginUseCase = LoginUseCase(loginRepository)
    )
}