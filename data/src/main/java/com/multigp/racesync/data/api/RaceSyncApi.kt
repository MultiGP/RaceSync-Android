package com.multigp.racesync.data.api

import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.UserInfo
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.requests.ChaptersRequest
import com.multigp.racesync.domain.model.requests.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface RaceSyncApi {
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BaseResponse2<UserInfo>

    @POST("chapter/list")
    suspend fun fetchChapters(
        @Body request: BaseRequest<ChaptersRequest>
    ): BaseResponse<List<Chapter>>
}