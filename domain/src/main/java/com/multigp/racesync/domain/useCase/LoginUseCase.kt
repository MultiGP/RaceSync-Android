package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.repositories.LoginRepository

class LoginUseCase(private val loginRepository: LoginRepository) {
    operator suspend fun invoke(params: LoginRequest) = loginRepository.login(params)
}