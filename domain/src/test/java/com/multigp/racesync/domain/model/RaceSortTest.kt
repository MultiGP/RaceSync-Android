package com.multigp.racesync.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RaceSortTest {

    @Test
    fun `sorts races earliest first`() {
        val list = listOf(
            race("c", "2026-05-10 09:00:00"),
            race("a", "2026-01-01 09:00:00"),
            race("b", "2026-03-15 09:00:00")
        )

        val ids = list.sortedByStartDateAscending().map { it.id }

        assertEquals(listOf("a", "b", "c"), ids)
    }

    @Test
    fun `races with null startDate sort after dated ones`() {
        val list = listOf(
            race("nullDate", startDate = null),
            race("dated", "2026-01-01 09:00:00")
        )

        val ids = list.sortedByStartDateAscending().map { it.id }

        assertEquals(listOf("dated", "nullDate"), ids)
    }

    @Test
    fun `races with unparseable startDate sort after valid dates`() {
        val list = listOf(
            race("garbage", "not-a-date"),
            race("valid", "2026-01-01 09:00:00")
        )

        val ids = list.sortedByStartDateAscending().map { it.id }

        assertEquals(listOf("valid", "garbage"), ids)
    }

    @Test
    fun `empty list returns empty`() {
        assertEquals(emptyList<Race>(), emptyList<Race>().sortedByStartDateAscending())
    }

    private fun race(id: String, startDate: String? = null): Race = Race(
        id = id,
        counter = 0,
        address = null,
        batteryRestriction = null,
        chapterId = null,
        chapterImageFileName = null,
        chapterName = "",
        city = null,
        content = null,
        country = null,
        courseId = null,
        courseName = null,
        dateAdded = null,
        dateModified = null,
        deleteAuth = null,
        description = null,
        latitude = null,
        longitude = null,
        mainImageFileName = null,
        name = id,
        ownerId = null,
        ownerUserName = null,
        propellerSizeRestriction = null,
        raceClass = null,
        raceClassString = null,
        raceEntryCount = null,
        scoringDisabled = null,
        seasonId = null,
        seasonName = null,
        sizeRestriction = null,
        startDate = startDate,
        endDate = null,
        state = null,
        status = null,
        typeRestriction = null,
        updateAuth = null,
        url = null,
        urlName = null,
        zip = null,
        childRaceCount = null
    )
}
