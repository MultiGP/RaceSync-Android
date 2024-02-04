package com.multigp.racesync.data.repository.dataSource

import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.LoginResponse
import retrofit2.Response

interface OnboardingDataSource {
    suspend fun login(body: LoginRequest): Response<LoginResponse>
}