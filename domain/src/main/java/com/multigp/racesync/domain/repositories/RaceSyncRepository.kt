package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.LoginResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RaceSyncRepository {
    suspend fun login(body: LoginRequest): Flow<Result<LoginResponse>>
}