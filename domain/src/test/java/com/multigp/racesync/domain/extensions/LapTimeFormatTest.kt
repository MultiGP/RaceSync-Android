package com.multigp.racesync.domain.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LapTimeFormatTest {

    @Test
    fun `sub-minute time formats with 3-decimal seconds`() {
        assertEquals("12.345s", formatLapTime("12.345"))
    }

    @Test
    fun `over-minute time formats as M_SS_mmm`() {
        assertEquals("1:02.500", formatLapTime("62.5"))
    }

    @Test
    fun `null input returns null`() {
        assertNull(formatLapTime(null))
    }

    @Test
    fun `non-numeric input returns null`() {
        assertNull(formatLapTime("not-a-number"))
    }

    @Test
    fun `zero or negative returns null`() {
        assertNull(formatLapTime("0"))
        assertNull(formatLapTime("-5"))
    }
}
