package com.multigp.racesync.domain.repositories

import androidx.paging.PagingData
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.BaseResponse
import kotlinx.coroutines.flow.Flow


interface ChaptersRepository {
    suspend fun fetchChapters(pilotId:String): Flow<PagingData<Chapter>>

    fun fetchChapter(chapterId: String): Flow<Chapter>

    suspend fun fetchPilotChapters(pilotId: String): Flow<List<Chapter>>
}