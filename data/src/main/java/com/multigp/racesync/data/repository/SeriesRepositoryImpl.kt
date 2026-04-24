package com.multigp.racesync.data.repository

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.repositories.SeriesRepository

private const val PAGE_SIZE = 100

class SeriesRepositoryImpl(
    private val raceSyncApi: RaceSyncApi,
    private val dataStore: DataStoreManager,
    private val apiKey: String
) : SeriesRepository {

    override suspend fun fetchSeries(): List<Series> {
        val response = raceSyncApi.fetchSeries(
            page = 0,
            pageSize = PAGE_SIZE,
            request = baseRequest()
        )

        if (!response.status) {
            throw Exception(response.errorMessage())
        }
        return response.data ?: emptyList()
    }

    override suspend fun fetchSeriesDetail(seriesId: String): Series {
        val response = raceSyncApi.fetchSeriesDetail(seriesId, baseRequest())

        if (!response.isSuccessful) {
            val errorResponse = BaseResponse.convertFromErrorResponse(response)
            throw Exception(errorResponse.statusDescription)
        }
        val body = response.body() ?: throw Exception("Empty response")
        if (!body.status) {
            throw Exception(body.errorMessage())
        }
        return body.data ?: throw Exception("Series not found")
    }

    private suspend fun baseRequest() = BaseRequest<Nothing>(
        apiKey = apiKey,
        sessionId = dataStore.getSessionId()!!
    )
}
