package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.repositories.LoginRepository

class LoginUseCase(private val loginRepository: LoginRepository) {
    operator suspend fun invoke(params: LoginRequest) = loginRepository.login(params)
    operator suspend fun invoke(action: String, facToken:String) = loginRepository.updateFCMToken(action, facToken)
    suspend fun logout() = loginRepository.logout()
    suspend fun clearSession() = loginRepository.clearSession()
}