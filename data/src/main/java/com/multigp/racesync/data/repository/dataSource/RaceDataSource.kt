package com.multigp.racesync.data.repository.dataSource

import com.multigp.racesync.data.paging.JoinedRacesPagingSources
import com.multigp.racesync.data.paging.NearbyRacesPagingSources
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.RaceRequest

interface RaceDataSource {
    fun fetchNearbyRaces(body: BaseRequest<RaceRequest>): NearbyRacesPagingSources

    fun fetchJoinedRaces(body: BaseRequest<RaceRequest>): JoinedRacesPagingSources
}