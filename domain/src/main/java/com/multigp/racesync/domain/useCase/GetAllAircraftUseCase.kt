package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.repositories.ProfileRepository

class GetAllAircraftUseCase (private val profileRepository: ProfileRepository) {

    operator suspend fun invoke (apiKey: String, pilotId:Int) = profileRepository.fetchAllAircraft(apiKey, pilotId)
}