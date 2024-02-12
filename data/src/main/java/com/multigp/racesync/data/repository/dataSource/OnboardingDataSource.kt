package com.multigp.racesync.data.repository.dataSource

import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.UserInfo

interface OnboardingDataSource {
    suspend fun login(body: LoginRequest): BaseResponse2<UserInfo>
}