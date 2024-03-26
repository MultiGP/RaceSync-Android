package com.multigp.racesync.data.repository.dataSource

import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.UserInfo
import com.multigp.racesync.domain.model.requests.LogoutRequest
import retrofit2.Response

interface OnboardingDataSource {
    suspend fun login(body: LoginRequest): BaseResponse2<UserInfo>
    suspend fun logout(body: LogoutRequest): Response<BaseResponse<Any>>
}