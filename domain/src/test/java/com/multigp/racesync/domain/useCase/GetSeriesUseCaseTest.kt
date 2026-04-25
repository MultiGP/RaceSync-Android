package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.repositories.SeriesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class GetSeriesUseCaseTest {

    @Test
    fun `invoke returns series list from repository`() = runBlocking {
        val expected = listOf(series("1"), series("2"))
        val useCase = GetSeriesUseCase(FakeRepository(listResult = expected))

        val actual = useCase()

        assertEquals(expected, actual)
    }

    @Test
    fun `detail returns series with matching id from repository`() = runBlocking {
        val expected = series("42", name = "Acme Series")
        val useCase = GetSeriesUseCase(FakeRepository(detailResult = expected))

        val actual = useCase.detail("42")

        assertEquals(expected, actual)
    }

    @Test
    fun `detail propagates repository errors`() {
        val useCase = GetSeriesUseCase(FakeRepository(detailError = RuntimeException("boom")))

        val error = assertThrows(RuntimeException::class.java) {
            runBlocking { useCase.detail("42") }
        }
        assertEquals("boom", error.message)
    }

    @Test
    fun `approveRace forwards both ids to repository`() = runBlocking {
        val repo = FakeRepository()
        val useCase = GetSeriesUseCase(repo)

        useCase.approveRace("s1", "r1")

        assertEquals(listOf("approve" to ("s1" to "r1")), repo.actions)
    }

    @Test
    fun `unapproveRace forwards both ids to repository`() = runBlocking {
        val repo = FakeRepository()
        val useCase = GetSeriesUseCase(repo)

        useCase.unapproveRace("s2", "r2")

        assertEquals(listOf("unapprove" to ("s2" to "r2")), repo.actions)
    }

    @Test
    fun `removeRaceFromSeries forwards both ids to repository`() = runBlocking {
        val repo = FakeRepository()
        val useCase = GetSeriesUseCase(repo)

        useCase.removeRaceFromSeries("s3", "r3")

        assertEquals(listOf("remove" to ("s3" to "r3")), repo.actions)
    }

    @Test
    fun `approver methods propagate repository errors`() {
        val repo = FakeRepository(actionError = RuntimeException("nope"))
        val useCase = GetSeriesUseCase(repo)

        assertTrue(
            assertThrows(RuntimeException::class.java) {
                runBlocking { useCase.approveRace("s", "r") }
            }.message == "nope"
        )
    }

    private class FakeRepository(
        private val listResult: List<Series> = emptyList(),
        private val detailResult: Series? = null,
        private val detailError: Throwable? = null,
        private val actionError: Throwable? = null
    ) : SeriesRepository {
        val actions = mutableListOf<Pair<String, Pair<String, String>>>()

        override suspend fun fetchSeries(): List<Series> = listResult
        override suspend fun fetchSeriesDetail(seriesId: String): Series {
            detailError?.let { throw it }
            return detailResult ?: error("no detailResult configured")
        }

        override suspend fun approveRace(seriesId: String, raceId: String) {
            actionError?.let { throw it }
            actions += "approve" to (seriesId to raceId)
        }

        override suspend fun unapproveRace(seriesId: String, raceId: String) {
            actionError?.let { throw it }
            actions += "unapprove" to (seriesId to raceId)
        }

        override suspend fun removeRaceFromSeries(seriesId: String, raceId: String) {
            actionError?.let { throw it }
            actions += "remove" to (seriesId to raceId)
        }
    }

    private fun series(id: String, name: String = id) = Series(id = id, name = name)
}
