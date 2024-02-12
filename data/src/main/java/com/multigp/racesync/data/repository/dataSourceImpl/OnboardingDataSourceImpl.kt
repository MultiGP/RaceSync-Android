package com.multigp.racesync.data.repository.dataSourceImpl

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.UserInfo

class OnboardingDataSourceImpl(
    private val raceSyncApi: RaceSyncApi
) : OnboardingDataSource {
    override suspend fun login(body: LoginRequest): BaseResponse2<UserInfo> {
        return raceSyncApi.login(body)
    }
}