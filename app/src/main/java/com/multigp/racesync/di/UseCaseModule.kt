package com.multigp.racesync.di

import com.multigp.racesync.domain.repositories.ChaptersRepository
import com.multigp.racesync.domain.repositories.LoginRepository
import com.multigp.racesync.domain.repositories.ProfileRepository
import com.multigp.racesync.domain.repositories.RacesRepository
import com.multigp.racesync.domain.useCase.GetAllAircraftUseCase
import com.multigp.racesync.domain.useCase.GetChaptersUseCase
import com.multigp.racesync.domain.useCase.GetLoginInfoUseCase
import com.multigp.racesync.domain.useCase.GetProfileUseCase
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
        racesRepository: RacesRepository,
        profileRepository: ProfileRepository
    ) = RaceSyncUseCases(
        performLoginUseCase = LoginUseCase(loginRepository),
        getLoginInfoUseCase = GetLoginInfoUseCase(loginRepository),
        getChaptersUseCase = GetChaptersUseCase(chaptersRepository, GetLoginInfoUseCase(loginRepository), GetProfileUseCase(profileRepository)),
        getRacesUseCase = GetRacesUseCase(racesRepository, GetLoginInfoUseCase(loginRepository), GetProfileUseCase(profileRepository)),
        getProfileUseCase = GetProfileUseCase(profileRepository),
        getAllAircraftUseCase = GetAllAircraftUseCase((profileRepository))
    )
}