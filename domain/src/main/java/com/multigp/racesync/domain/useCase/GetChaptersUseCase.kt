package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.repositories.ChaptersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetChaptersUseCase(
    private val chaptersRepository: ChaptersRepository,
    private val loginInfoUserCase: GetLoginInfoUserCase
) {
    operator suspend fun invoke() = chaptersRepository.fetchChapters()
    operator suspend fun invoke(radius: Double) = chaptersRepository.fetchChapters(radius)
    suspend fun fetchJoinedChapters(): Flow<Result<BaseResponse<List<Chapter>>>> {
        val loginInfo = loginInfoUserCase().first()
        return chaptersRepository.fetchChapters(loginInfo.second.id)
    }
}