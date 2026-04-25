package com.multigp.racesync.data.repository

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.RaceIdRequest
import com.multigp.racesync.domain.repositories.SeriesRepository
import retrofit2.Response

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

    override suspend fun approveRace(seriesId: String, raceId: String) {
        unwrap(raceSyncApi.approveRaceInSeries(seriesId, raceIdRequest(raceId)))
    }

    override suspend fun unapproveRace(seriesId: String, raceId: String) {
        unwrap(raceSyncApi.unapproveRaceInSeries(seriesId, raceIdRequest(raceId)))
    }

    override suspend fun removeRaceFromSeries(seriesId: String, raceId: String) {
        unwrap(raceSyncApi.removeRaceFromSeries(seriesId, raceIdRequest(raceId)))
    }

    /** Throws on transport / HTTP / business-logic failure; returns silently on success. */
    private fun unwrap(response: Response<BaseResponse<Any>>) {
        if (!response.isSuccessful) {
            val errorResponse = BaseResponse.convertFromErrorResponse(response)
            throw Exception(errorResponse.statusDescription)
        }
        val body = response.body() ?: throw Exception("Empty response")
        if (!body.status) {
            throw Exception(body.errorMessage())
        }
    }

    private suspend fun baseRequest() = BaseRequest<Nothing>(
        apiKey = apiKey,
        sessionId = dataStore.getSessionId()!!
    )

    private suspend fun raceIdRequest(raceId: String) = BaseRequest(
        apiKey = apiKey,
        data = RaceIdRequest(raceId),
        sessionId = dataStore.getSessionId()!!
    )
}
