package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.BaseResponse
import kotlinx.coroutines.flow.Flow


interface ChaptersRepository {
    suspend fun fetchChapters(): Flow<Result<BaseResponse<List<Chapter>>>>

    suspend fun fetchChapters(radius: Double): Flow<Result<BaseResponse<List<Chapter>>>>

    suspend fun fetchChapters(pilotId: String): Flow<Result<BaseResponse<List<Chapter>>>>

}