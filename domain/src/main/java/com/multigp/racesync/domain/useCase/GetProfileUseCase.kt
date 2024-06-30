package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.repositories.ProfileRepository

class GetProfileUseCase (private val profileRepository: ProfileRepository){
    suspend operator fun invoke() = profileRepository.fetchProfile()
    suspend operator fun invoke(pilotName: String) = profileRepository.fetchProfile(pilotName)
    suspend fun getPilotId(pilotUserName: String) = profileRepository.getPilotId(pilotUserName)

}