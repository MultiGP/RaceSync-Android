package com.multigp.racesync.domain.useCase

import androidx.paging.PagingData
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.repositories.ChaptersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetChaptersUseCase(
    private val chaptersRepository: ChaptersRepository,
    private val loginInfoUserCase: GetLoginInfoUseCase,
    private val profileUseCase: GetProfileUseCase,
) {
    suspend operator fun invoke(): Flow<PagingData<Chapter>> {
        val loginInfo = loginInfoUserCase().first()
        return chaptersRepository.fetchChapters(loginInfo.second!!.id)
    }

    operator fun invoke(chapterId: String) = chaptersRepository.fetchChapter(chapterId)

    suspend fun fetchPilotChapters(pilotUserName:String): Flow<List<Chapter>> {
        profileUseCase.getPilotId(pilotUserName)?.let {pilotId ->
            return chaptersRepository.fetchPilotChapters(pilotId)
        } ?: kotlin.run {
            throw Exception("Invalid pilot username")
        }
    }
}