package com.multigp.racesync.domain.useCase

data class RaceSyncUseCases(
    val performLoginUseCase: LoginUseCase,
    val getLoginInfoUserCase: GetLoginInfoUserCase,
    val getChaptersUseCase: GetChaptersUseCase,
    val getRacesUseCase: GetRacesUseCase
)