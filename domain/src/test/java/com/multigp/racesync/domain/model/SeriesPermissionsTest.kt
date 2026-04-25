package com.multigp.racesync.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SeriesPermissionsTest {

    @Test
    fun `canBeEditedBy returns true when ids match`() {
        assertTrue(series(ownerId = "u1").canBeEditedBy("u1"))
    }

    @Test
    fun `canBeEditedBy returns false when ids differ`() {
        assertFalse(series(ownerId = "u1").canBeEditedBy("u2"))
    }

    @Test
    fun `canBeEditedBy returns false when user id is null or blank`() {
        assertFalse(series(ownerId = "u1").canBeEditedBy(null))
        assertFalse(series(ownerId = "u1").canBeEditedBy(""))
        assertFalse(series(ownerId = "u1").canBeEditedBy("   "))
    }

    @Test
    fun `canBeEditedBy returns false when series owner is null`() {
        assertFalse(series(ownerId = null).canBeEditedBy("u1"))
    }

    private fun series(ownerId: String?): Series =
        Series(id = "s1", name = "S", ownerId = ownerId)
}
