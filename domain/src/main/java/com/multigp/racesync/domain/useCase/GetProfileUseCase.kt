package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.repositories.ProfileRepository

class GetProfileUseCase (private val profileRepository: ProfileRepository){
    suspend operator fun invoke() = profileRepository.fetchProfile()



}