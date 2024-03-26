package com.multigp.racesync.domain.useCase

data class RaceSyncUseCases(
    val performLoginUseCase: LoginUseCase,
    val getLoginInfoUseCase: GetLoginInfoUseCase,
    val getChaptersUseCase: GetChaptersUseCase,
    val getRacesUseCase: GetRacesUseCase,
    val getProfileUseCase: GetProfileUseCase,
    val getAllAircraftUseCase: GetAllAircraftUseCase
)