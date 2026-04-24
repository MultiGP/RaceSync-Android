package com.multigp.racesync.domain.model

import java.util.Calendar

/**
 * Mirrors iOS `SeriesFilter` (SeriesFeedController.swift):
 *   - Joined    → user's joined series, sorted recent-first
 *   - Regionals → scoreType == regionals, joined-first then default sort
 *   - All       → everything, sorted recent-first
 *
 * iOS default is Regionals on first launch.
 */
enum class SeriesFilter(val title: String) {
    Joined("My Series"),
    Regionals("Regionals"),
    All("All Series");

    companion object {
        val Default: SeriesFilter = Regionals
    }
}

/** Applies the iOS per-filter selection + sort rules to a raw series list. */
fun List<Series>.filteredAndSorted(filter: SeriesFilter): List<Series> {
    return when (filter) {
        SeriesFilter.Joined ->
            filter { it.isJoined }.sortedSeries(prioritizeRecent = true)
        SeriesFilter.Regionals ->
            filter { it.isRegional }.sortedSeries(prioritizeJoined = true)
        SeriesFilter.All ->
            sortedSeries(prioritizeRecent = true)
    }
}

/**
 * Ports iOS SeriesFeedController.sortedSeries:
 *   1. (optional) joined first
 *   2. ended (endDate < now) always last
 *   3. no participation (pilotCount == 0) just before ended
 *   4. (optional) recency by year of endDate ?: startDate, desc
 *   5. popularity (pilotCount desc) as tie-breaker
 */
private fun List<Series>.sortedSeries(
    prioritizeJoined: Boolean = false,
    prioritizeRecent: Boolean = false
): List<Series> {
    val now = System.currentTimeMillis()
    val calendar = Calendar.getInstance()

    fun year(series: Series): Int? {
        val date = series.endDate ?: series.startDate ?: return null
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    val comparator = Comparator<Series> { a, b ->
        val aEnded = a.endDate?.let { it.time < now } ?: false
        val bEnded = b.endDate?.let { it.time < now } ?: false
        val aNoParticipation = a.pilotCount == 0
        val bNoParticipation = b.pilotCount == 0

        if (prioritizeJoined && a.isJoined != b.isJoined) {
            return@Comparator if (a.isJoined) -1 else 1
        }
        if (aEnded != bEnded) return@Comparator if (aEnded) 1 else -1
        if (aNoParticipation != bNoParticipation) return@Comparator if (aNoParticipation) 1 else -1

        if (prioritizeRecent) {
            val ay = year(a)
            val by = year(b)
            if (ay != null && by != null && ay != by) {
                return@Comparator by.compareTo(ay)
            }
            if ((ay == null) != (by == null)) {
                return@Comparator if (ay == null) 1 else -1
            }
        }

        b.pilotCount.compareTo(a.pilotCount)
    }
    return sortedWith(comparator)
}
