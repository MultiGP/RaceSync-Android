package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.requests.ProfileRequest
import com.multigp.racesync.domain.repositories.ProfileRepository

class GetProfileUseCase (private val profileRepository: ProfileRepository){
    operator suspend fun invoke(params: String) = profileRepository.fetchProfile(params)

}