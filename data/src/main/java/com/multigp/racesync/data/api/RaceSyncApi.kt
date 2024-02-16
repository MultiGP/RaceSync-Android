package com.multigp.racesync.data.api

import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.UserInfo
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.ChaptersRequest
import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.requests.RaceRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RaceSyncApi {
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BaseResponse2<UserInfo>

    @POST("chapter/list")
    suspend fun fetchChapters(
        @Body request: BaseRequest<ChaptersRequest>
    ): BaseResponse<List<Chapter>>


    @POST("race/list")
    suspend fun fetchRaces(
        @Query("currentPage") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body request: BaseRequest<RaceRequest>
    ): BaseResponse<List<Race>>
}