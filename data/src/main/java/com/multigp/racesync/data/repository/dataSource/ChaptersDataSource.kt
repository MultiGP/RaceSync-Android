package com.multigp.racesync.data.repository.dataSource

import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.requests.ChaptersRequest

interface ChaptersDataSource {
    suspend fun fetchChapters(body: BaseRequest<ChaptersRequest>): BaseResponse<List<Chapter>>
}