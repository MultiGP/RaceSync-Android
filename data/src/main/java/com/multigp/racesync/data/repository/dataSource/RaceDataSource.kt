package com.multigp.racesync.data.repository.dataSource

import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.RaceRequest

interface RaceDataSource {
    suspend fun fetchRaces(body: BaseRequest<RaceRequest>): BaseResponse<List<Race>>
}