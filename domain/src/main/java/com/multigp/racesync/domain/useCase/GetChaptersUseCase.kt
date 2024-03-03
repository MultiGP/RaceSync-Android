package com.multigp.racesync.domain.useCase

import androidx.paging.PagingData
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.repositories.ChaptersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetChaptersUseCase(
    private val chaptersRepository: ChaptersRepository,
    private val loginInfoUserCase: GetLoginInfoUserCase
) {
    suspend operator fun invoke(): Flow<PagingData<Chapter>> {
        val loginInfo = loginInfoUserCase().first()
        return chaptersRepository.fetchChapters(loginInfo.second.id)
    }

    operator fun invoke(chapterId: String) = chaptersRepository.fetchChapter(chapterId)

}