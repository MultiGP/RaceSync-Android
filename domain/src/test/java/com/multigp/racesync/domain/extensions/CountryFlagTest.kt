package com.multigp.racesync.domain.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class CountryFlagTest {

    @Test
    fun `returns flag emoji for valid 2-letter code`() {
        // 🇺🇸
        assertEquals("🇺🇸", countryToFlag("US"))
    }

    @Test
    fun `lowercase code is normalised`() {
        assertEquals(countryToFlag("US"), countryToFlag("us"))
    }

    @Test
    fun `null code returns empty`() {
        assertEquals("", countryToFlag(null))
    }

    @Test
    fun `wrong length returns empty`() {
        assertEquals("", countryToFlag("U"))
        assertEquals("", countryToFlag("USA"))
    }

    @Test
    fun `non-letters return empty`() {
        assertEquals("", countryToFlag("U1"))
        assertEquals("", countryToFlag("12"))
    }
}
