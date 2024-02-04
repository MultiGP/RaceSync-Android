package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.repositories.RaceSyncRepository

class LoginUseCase(private val raceSyncRepository: RaceSyncRepository) {
    operator suspend fun invoke(params: LoginRequest) = raceSyncRepository.login(params)
}