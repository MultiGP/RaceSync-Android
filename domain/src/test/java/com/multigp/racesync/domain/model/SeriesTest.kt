package com.multigp.racesync.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SeriesTest {

    @Test
    fun `parses RRGGBB hex with hash prefix`() {
        val argb = parseHexColorOrNull("#AABBCC")
        assertEquals(0xFFAABBCC.toInt(), argb)
    }

    @Test
    fun `parses RRGGBB hex without hash prefix`() {
        val argb = parseHexColorOrNull("AABBCC")
        assertEquals(0xFFAABBCC.toInt(), argb)
    }

    @Test
    fun `parses AARRGGBB hex preserving alpha`() {
        val argb = parseHexColorOrNull("#80AABBCC")
        assertEquals(0x80AABBCC.toInt(), argb)
    }

    @Test
    fun `returns null for null input`() {
        assertNull(parseHexColorOrNull(null))
    }

    @Test
    fun `returns null for blank input`() {
        assertNull(parseHexColorOrNull("   "))
    }

    @Test
    fun `returns null for wrong length`() {
        assertNull(parseHexColorOrNull("#FFF"))
        assertNull(parseHexColorOrNull("#FFFFF"))
    }

    @Test
    fun `returns null for non-hex characters`() {
        assertNull(parseHexColorOrNull("#ZZZZZZ"))
    }

    @Test
    fun `trims surrounding whitespace`() {
        val argb = parseHexColorOrNull("  #AABBCC  ")
        assertEquals(0xFFAABBCC.toInt(), argb)
    }
}
