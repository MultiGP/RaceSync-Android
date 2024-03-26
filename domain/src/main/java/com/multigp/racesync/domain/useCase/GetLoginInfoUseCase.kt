package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.repositories.LoginRepository

class GetLoginInfoUseCase(private val loginRepository: LoginRepository) {
    operator suspend fun invoke() = loginRepository.getLoginInfo()
}