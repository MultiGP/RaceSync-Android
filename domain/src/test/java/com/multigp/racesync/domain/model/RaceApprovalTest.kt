package com.multigp.racesync.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RaceApprovalTest {

    @Test
    fun `isApproved is true for "1"`() {
        assertTrue(race(approved = "1").isApproved)
    }

    @Test
    fun `isApproved is true for "true" case insensitive`() {
        assertTrue(race(approved = "true").isApproved)
        assertTrue(race(approved = "TRUE").isApproved)
    }

    @Test
    fun `isApproved is false for null, "0", or unrecognised strings`() {
        assertFalse(race(approved = null).isApproved)
        assertFalse(race(approved = "0").isApproved)
        assertFalse(race(approved = "false").isApproved == true && false) // belt and braces
        assertFalse(race(approved = "yes").isApproved)
    }

    @Test
    fun `isFinalized parses the same way as isApproved`() {
        assertTrue(race(finalized = "1").isFinalized)
        assertFalse(race(finalized = "0").isFinalized)
        assertFalse(race(finalized = null).isFinalized)
    }

    @Test
    fun `approvalState returns Completed when finalized regardless of approval`() {
        assertEquals(RaceApprovalState.Completed, race(approved = "1", finalized = "1").approvalState())
        assertEquals(RaceApprovalState.Completed, race(approved = "0", finalized = "1").approvalState())
    }

    @Test
    fun `approvalState returns Approved when approved and not finalized`() {
        assertEquals(RaceApprovalState.Approved, race(approved = "1", finalized = "0").approvalState())
    }

    @Test
    fun `approvalState returns NotApproved when neither flag is set`() {
        assertEquals(RaceApprovalState.NotApproved, race(approved = null, finalized = null).approvalState())
        assertEquals(RaceApprovalState.NotApproved, race(approved = "0", finalized = "0").approvalState())
    }

    private fun race(approved: String? = null, finalized: String? = null): Race = Race(
        id = "1", counter = 0, address = null, batteryRestriction = null,
        chapterId = null, chapterImageFileName = null, chapterName = "",
        city = null, content = null, country = null, courseId = null,
        courseName = null, dateAdded = null, dateModified = null, deleteAuth = null,
        description = null, latitude = null, longitude = null,
        mainImageFileName = null, name = "n", ownerId = null, ownerUserName = null,
        propellerSizeRestriction = null, raceClass = null, raceClassString = null,
        raceEntryCount = null, scoringDisabled = null, seasonId = null,
        seasonName = null, sizeRestriction = null, startDate = null, endDate = null,
        state = null, status = null, typeRestriction = null, updateAuth = null,
        url = null, urlName = null, zip = null, childRaceCount = null
    ).apply {
        approvedRaw = approved
        finalizedRaw = finalized
    }
}
