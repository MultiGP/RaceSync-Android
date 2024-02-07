package com.multigp.racesync.data.api

import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RaceSyncApi {
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}