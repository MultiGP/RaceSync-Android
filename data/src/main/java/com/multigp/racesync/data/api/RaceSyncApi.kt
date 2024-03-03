package com.multigp.racesync.data.api

import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.AircraftResponse
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.UserInfo
import com.multigp.racesync.domain.model.requests.AircraftRequest
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.ChaptersRequest
import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.requests.ProfileRequest
import com.multigp.racesync.domain.model.requests.RaceRequest
import kotlinx.coroutines.flow.Flow
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
        @Query("currentPage") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body request: BaseRequest<ChaptersRequest>
    ): BaseResponse<List<Chapter>>


    @POST("race/list")
    suspend fun fetchRaces(
        @Query("currentPage") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body request: BaseRequest<RaceRequest>
    ): BaseResponse<List<Race>>

    @POST("race/listForChapter")
    suspend fun fetchRacesForChapter(
        @Query("chapterId") chapterId: String,
        @Body request: BaseRequest<Any>
    ): BaseResponse<List<Race>>

    @POST("user/profile")
    suspend fun fetchProfile(
        @Body request: ProfileRequest
    ): BaseResponse<Profile>


    @POST("aircraft/list")
    suspend fun fetchAllAircraft(
        @Body request: AircraftRequest
    ): BaseResponse<List<Aircraft>>
}