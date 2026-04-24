package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.repositories.SeriesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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

    private class FakeRepository(
        private val listResult: List<Series> = emptyList(),
        private val detailResult: Series? = null,
        private val detailError: Throwable? = null
    ) : SeriesRepository {
        override suspend fun fetchSeries(): List<Series> = listResult
        override suspend fun fetchSeriesDetail(seriesId: String): Series {
            detailError?.let { throw it }
            return detailResult ?: error("no detailResult configured")
        }
    }

    private fun series(id: String, name: String = id) = Series(id = id, name = name)
}
