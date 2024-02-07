package com.multigp.racesync.data.repository

import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.LoginResponse
import com.multigp.racesync.domain.repositories.RaceSyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RaceSyncRepositoryImpl(private val onboardingDataSource: OnboardingDataSource) :
    RaceSyncRepository {
    override suspend fun login(body: LoginRequest): Flow<Result<LoginResponse>> = flow {
        val loginResponse = onboardingDataSource.login(body)
        emit(Result.success(loginResponse))
    }
        .catch { emit(Result.failure(it)) }
        .flowOn(Dispatchers.IO)

}