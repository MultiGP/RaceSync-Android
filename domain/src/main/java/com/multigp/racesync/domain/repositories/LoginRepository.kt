package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.LoginResponse
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(body: LoginRequest): Flow<Result<LoginResponse>>
}