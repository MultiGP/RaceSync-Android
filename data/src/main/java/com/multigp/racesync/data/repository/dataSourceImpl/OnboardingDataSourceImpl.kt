package com.multigp.racesync.data.repository.dataSourceImpl

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.LoginResponse
import retrofit2.Response

class OnboardingDataSourceImpl(
    private val raceSyncApi: RaceSyncApi
) : OnboardingDataSource {
    override suspend fun login(body: LoginRequest): LoginResponse {
        return raceSyncApi.login(body)
    }
}