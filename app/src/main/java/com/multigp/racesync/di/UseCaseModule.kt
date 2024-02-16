package com.multigp.racesync.di

import com.multigp.racesync.domain.repositories.ChaptersRepository
import com.multigp.racesync.domain.repositories.LoginRepository
import com.multigp.racesync.domain.repositories.RacesRepository
import com.multigp.racesync.domain.useCase.GetChaptersUseCase
import com.multigp.racesync.domain.useCase.GetLoginInfoUserCase
import com.multigp.racesync.domain.useCase.GetRacesUseCase
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
    fun provideRaceSyncUseCases(
        loginRepository: LoginRepository,
        chaptersRepository: ChaptersRepository,
        racesRepository: RacesRepository
    ) = RaceSyncUseCases(
        performLoginUseCase = LoginUseCase(loginRepository),
        getLoginInfoUserCase = GetLoginInfoUserCase(loginRepository),
        getChaptersUseCase = GetChaptersUseCase(chaptersRepository, GetLoginInfoUserCase(loginRepository)),
        getRacesUseCase = GetRacesUseCase(racesRepository, GetLoginInfoUserCase(loginRepository))
    )
}