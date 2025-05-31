package com.multigp.racesync.data.repository.dataSourceImpl

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.UserInfo
import com.multigp.racesync.domain.model.requests.LogoutRequest
import com.multigp.racesync.domain.model.requests.UpdateFCMTokenRequest
import retrofit2.Response

class OnboardingDataSourceImpl(
    private val raceSyncApi: RaceSyncApi
) : OnboardingDataSource {
    override suspend fun login(body: LoginRequest): BaseResponse2<UserInfo> {
        return raceSyncApi.login(body)
    }

    override suspend fun logout(body: LogoutRequest): Response<BaseResponse<Any>> {
        return raceSyncApi.logout(body)
    }

    override suspend fun updateFCMToken(apiKey: String, sessionId: String, body: UpdateFCMTokenRequest): Response<BaseResponse<Any>> {
        return raceSyncApi.updateFCMToken(apiKey, sessionId, body)
    }
}