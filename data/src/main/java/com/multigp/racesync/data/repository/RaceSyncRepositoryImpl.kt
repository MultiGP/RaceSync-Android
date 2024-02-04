package com.multigp.racesync.data.repository

import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.LoginResponse
import com.multigp.racesync.domain.repositories.RaceSyncRepository
import retrofit2.Response

class RaceSyncRepositoryImpl(private val onboardingDataSource: OnboardingDataSource) :
    RaceSyncRepository {
    override suspend fun login(body: LoginRequest): Response<LoginResponse> {
        return onboardingDataSource.login(body)
    }
}