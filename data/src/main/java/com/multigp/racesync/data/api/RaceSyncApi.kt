package com.multigp.racesync.data.api

import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.AircraftResponse
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Pilot
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView
import com.multigp.racesync.domain.model.UserInfo
import com.multigp.racesync.domain.model.requests.AircraftRequest
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.ChaptersRequest
import com.multigp.racesync.domain.model.requests.JoinRaceRequest
import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.requests.LogoutRequest
import com.multigp.racesync.domain.model.requests.ProfileRequest
import com.multigp.racesync.domain.model.requests.RaceRequest
import com.multigp.racesync.domain.model.requests.SearchRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RaceSyncApi {
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BaseResponse2<UserInfo>


    @POST("user/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<BaseResponse<Any>>

    @POST("chapter/list")
    suspend fun fetchChapters(
        @Query("currentPage") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body request: BaseRequest<ChaptersRequest>
    ): BaseResponse<List<Chapter>>

    @POST("chapter/list")
    suspend fun fetchChapters2(
        @Query("currentPage") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body request: BaseRequest<ChaptersRequest>
    ): Response<BaseResponse<List<Chapter>>>

    @POST("race/list")
    suspend fun fetchRaces(
        @Query("currentPage") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body request: BaseRequest<RaceRequest>
    ): BaseResponse<List<Race>>

    @POST("race/list")
    suspend fun fetchRaces2(
        @Query("currentPage") page: Int,
        @Query("pageSize") pageSize: Int,
        @Body request: BaseRequest<RaceRequest>
    ): Response<BaseResponse<List<Race>>>

    @POST("race/listForChapter")
    suspend fun fetchRacesForChapter(
        @Query("chapterId") chapterId: String,
        @Body request: BaseRequest<Any>
    ): BaseResponse<List<Race>>

    @POST("user/profile")
    suspend fun fetchProfile(
        @Body request: ProfileRequest
    ): Response<BaseResponse<Profile>>

    @POST("user/search")
    suspend fun searchUser(
        @Body request: BaseRequest<SearchRequest>
    ): Response<BaseResponse<Profile>>

    @POST("aircraft/list")
    suspend fun fetchAllAircraft(
        @Body request: AircraftRequest
    ): BaseResponse<List<Aircraft>>

    @POST("race/join")
    suspend fun joinRace(
        @Query("id") raceId: String,
        @Body request: BaseRequest<JoinRaceRequest>
    ): Response<BaseResponse<Any>>

    @POST("race/resign")
    suspend fun resignFromRace(
        @Query("id") raceId: String,
        @Body request: BaseRequest<Nothing>
    ): Response<BaseResponse<Any>>

    @POST("race/view")
    suspend fun fetchRaceView(
        @Query("id") raceId: String,
        @Body request: BaseRequest<Nothing>
    ): Response<BaseResponse<RaceView>>
}