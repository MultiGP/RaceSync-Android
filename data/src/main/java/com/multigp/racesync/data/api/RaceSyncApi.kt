package com.multigp.racesync.data.api

import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.LoginResponse
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Body

interface RaceSyncApi {
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}