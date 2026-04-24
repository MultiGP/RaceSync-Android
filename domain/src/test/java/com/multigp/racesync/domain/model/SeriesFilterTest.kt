package com.multigp.racesync.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

class SeriesFilterTest {

    @Test
    fun `Joined filter keeps only joined series`() {
        val list = listOf(
            series("a", isJoined = true),
            series("b", isJoined = false),
            series("c", isJoined = true)
        )

        val ids = list.filteredAndSorted(SeriesFilter.Joined).map { it.id }

        assertEquals(listOf("a", "c"), ids)
    }

    @Test
    fun `Regionals filter keeps only regional scoreType`() {
        val list = listOf(
            series("a", scoreType = Series.SCORE_TYPE_REGIONAL),
            series("b", scoreType = "1"),
            series("c", scoreType = Series.SCORE_TYPE_REGIONAL)
        )

        val ids = list.filteredAndSorted(SeriesFilter.Regionals).map { it.id }

        assertEquals(listOf("a", "c"), ids)
    }

    @Test
    fun `All filter keeps everything`() {
        val list = listOf(
            series("a"),
            series("b"),
            series("c")
        )

        val ids = list.filteredAndSorted(SeriesFilter.All).map { it.id }.sorted()

        assertEquals(listOf("a", "b", "c"), ids)
    }

    @Test
    fun `Ended series sort after active ones`() {
        val list = listOf(
            series("ended", endDate = yearsAgo(1)),
            series("active", endDate = yearsAhead(1))
        )

        val ids = list.filteredAndSorted(SeriesFilter.All).map { it.id }

        assertEquals(listOf("active", "ended"), ids)
    }

    @Test
    fun `Empty series sort after populated but before ended`() {
        val list = listOf(
            series("ended", endDate = yearsAgo(1), pilotCount = 50),
            series("empty", endDate = yearsAhead(1), pilotCount = 0),
            series("populated", endDate = yearsAhead(1), pilotCount = 50)
        )

        val ids = list.filteredAndSorted(SeriesFilter.All).map { it.id }

        assertEquals(listOf("populated", "empty", "ended"), ids)
    }

    @Test
    fun `Higher pilotCount wins as tie-breaker`() {
        val list = listOf(
            series("low", pilotCount = 10, endDate = yearsAhead(1)),
            series("high", pilotCount = 100, endDate = yearsAhead(1))
        )

        val ids = list.filteredAndSorted(SeriesFilter.All).map { it.id }

        assertEquals(listOf("high", "low"), ids)
    }

    @Test
    fun `Recent year first when prioritizeRecent set`() {
        val list = listOf(
            series("old", startDate = yearsAgoStart(3), endDate = yearsAgoStart(2), pilotCount = 100),
            series("new", startDate = yearsAhead(1), endDate = yearsAhead(2), pilotCount = 10)
        )

        val ids = list.filteredAndSorted(SeriesFilter.All).map { it.id }

        assertEquals(listOf("new", "old"), ids)
    }

    @Test
    fun `Regionals filter puts joined regionals first`() {
        val list = listOf(
            series("notJoined", scoreType = Series.SCORE_TYPE_REGIONAL, isJoined = false, pilotCount = 100, endDate = yearsAhead(1)),
            series("joined", scoreType = Series.SCORE_TYPE_REGIONAL, isJoined = true, pilotCount = 10, endDate = yearsAhead(1))
        )

        val ids = list.filteredAndSorted(SeriesFilter.Regionals).map { it.id }

        assertEquals(listOf("joined", "notJoined"), ids)
    }

    @Test
    fun `Default filter is Regionals`() {
        assertEquals(SeriesFilter.Regionals, SeriesFilter.Default)
    }

    // region helpers

    private fun series(
        id: String,
        name: String = id,
        isJoined: Boolean = false,
        scoreType: String? = null,
        pilotCount: Int = 0,
        startDate: Date? = null,
        endDate: Date? = null
    ) = Series(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        scoreType = scoreType,
        mainImageUrl = null,
        isJoined = isJoined,
        pilotCount = pilotCount
    )

    private fun yearsAgo(years: Int): Date =
        Calendar.getInstance().apply { add(Calendar.YEAR, -years) }.time

    private fun yearsAhead(years: Int): Date =
        Calendar.getInstance().apply { add(Calendar.YEAR, years) }.time

    private fun yearsAgoStart(years: Int): Date =
        Calendar.getInstance().apply {
            add(Calendar.YEAR, -years)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
        }.time

    // endregion
}
