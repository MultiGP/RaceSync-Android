package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.repositories.ProfileRepository

class GetAllAircraftUseCase (
    private val profileRepository: ProfileRepository
) {
    operator suspend fun invoke () = profileRepository.fetchAllAircraft()

    operator suspend fun invoke (pilotId: String) = profileRepository.fetchPilotAircrafts(pilotId)
}