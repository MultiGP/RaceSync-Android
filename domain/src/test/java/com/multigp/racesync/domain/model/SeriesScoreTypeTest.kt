package com.multigp.racesync.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SeriesScoreTypeTest {

    @Test
    fun `parses each documented raw value`() {
        assertEquals(SeriesScoreType.Overall, SeriesScoreType.fromRaw("0"))
        assertEquals(SeriesScoreType.Collegiate, SeriesScoreType.fromRaw("1"))
        assertEquals(SeriesScoreType.ProSpec, SeriesScoreType.fromRaw("2"))
        assertEquals(SeriesScoreType.Fastest3Laps, SeriesScoreType.fromRaw("3"))
        assertEquals(SeriesScoreType.Regionals, SeriesScoreType.fromRaw("4"))
    }

    @Test
    fun `unknown raw falls back to Overall`() {
        assertEquals(SeriesScoreType.Overall, SeriesScoreType.fromRaw("99"))
        assertEquals(SeriesScoreType.Overall, SeriesScoreType.fromRaw(""))
        assertEquals(SeriesScoreType.Overall, SeriesScoreType.fromRaw(null))
    }
}
