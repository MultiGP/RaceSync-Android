package com.multigp.racesync.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SeriesResultLabelsTest {

    // ── Overall ──

    @Test
    fun `Overall pilot row shows races + elo subtitle and pts score`() {
        val labels = seriesResultLabels(
            pilotResult(score = 42.0, eloScore = 1200, raceCount = 5),
            SeriesScoreType.Overall
        )
        assertEquals("Races: 5  |  Elo: 1200", labels.subtitle)
        assertEquals("42 pts", labels.score)
    }

    @Test
    fun `Overall chapter row omits elo`() {
        val labels = seriesResultLabels(
            chapterResult(score = 100.0, raceCount = 3),
            SeriesScoreType.Overall
        )
        assertEquals("Races: 3", labels.subtitle)
        assertEquals("100 pts", labels.score)
    }

    @Test
    fun `Overall row with score of one uses singular pt`() {
        val labels = seriesResultLabels(
            pilotResult(score = 1.0, eloScore = 1000, raceCount = 1),
            SeriesScoreType.Overall
        )
        assertEquals("1 pt", labels.score)
    }

    @Test
    fun `Overall row with zero races omits the races part`() {
        val labels = seriesResultLabels(
            pilotResult(score = 5.0, eloScore = 800, raceCount = 0),
            SeriesScoreType.Overall
        )
        assertEquals("Elo: 800", labels.subtitle)
    }

    @Test
    fun `Regionals reuses Overall formatting`() {
        val overall = seriesResultLabels(pilotResult(score = 10.0, eloScore = 900, raceCount = 2), SeriesScoreType.Overall)
        val regionals = seriesResultLabels(pilotResult(score = 10.0, eloScore = 900, raceCount = 2), SeriesScoreType.Regionals)
        assertEquals(overall, regionals)
    }

    // ── Fastest3Laps ──

    @Test
    fun `Fastest3Laps shows formatted lap time and no score`() {
        val labels = seriesResultLabels(
            pilotResult(fastest3Laps = "12.5"),
            SeriesScoreType.Fastest3Laps
        )
        assertEquals("12.500s", labels.subtitle)
        assertEquals("", labels.score)
        assertEquals(false, labels.hasScore)
    }

    @Test
    fun `Fastest3Laps without time falls back to placeholder`() {
        val labels = seriesResultLabels(
            pilotResult(fastest3Laps = null),
            SeriesScoreType.Fastest3Laps
        )
        assertEquals(SeriesResultLabels.EMPTY_PLACEHOLDER, labels.subtitle)
    }

    // ── Collegiate ──

    @Test
    fun `Collegiate pilot subtitle is lap time, score has up to 3 decimals`() {
        val labels = seriesResultLabels(
            pilotResult(fastest3Laps = "30", score = 12.5),
            SeriesScoreType.Collegiate
        )
        assertEquals("30.000s", labels.subtitle)
        assertEquals("12.5", labels.score)
    }

    @Test
    fun `Collegiate chapter subtitle is best results array`() {
        val labels = seriesResultLabels(
            chapterResult(bestResults = listOf(8.0, 9.5, 10.25), score = 5.0),
            SeriesScoreType.Collegiate
        )
        assertEquals("Best: [8, 9.5, 10.25]", labels.subtitle)
        assertEquals("5", labels.score)
    }

    @Test
    fun `Collegiate score is hidden when zero`() {
        val labels = seriesResultLabels(
            pilotResult(fastest3Laps = "20", score = 0.0),
            SeriesScoreType.Collegiate
        )
        assertEquals("", labels.score)
    }

    @Test
    fun `Collegiate with no time and no best falls back to placeholder`() {
        val labels = seriesResultLabels(
            pilotResult(fastest3Laps = null, score = 5.0),
            SeriesScoreType.Collegiate
        )
        assertEquals(SeriesResultLabels.EMPTY_PLACEHOLDER, labels.subtitle)
    }

    // ── helpers ──

    private fun pilotResult(
        score: Double = 0.0,
        eloScore: Int = 0,
        raceCount: Int = 0,
        fastest3Laps: String? = null
    ) = SeriesResult(
        pilotId = "p",
        userName = "Pilot",
        score = score,
        eloScore = eloScore,
        raceCount = raceCount,
        fastest3Laps = fastest3Laps
    )

    private fun chapterResult(
        score: Double = 0.0,
        raceCount: Int = 0,
        bestResults: List<Double>? = null
    ) = SeriesResult(
        chapterId = "c",
        chapterName = "Chapter",
        score = score,
        raceCount = raceCount,
        bestResults = bestResults
    )
}
