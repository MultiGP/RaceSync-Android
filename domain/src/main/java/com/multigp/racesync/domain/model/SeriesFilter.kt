package com.multigp.racesync.domain.model

import java.util.Calendar

/**
 * User-visible filter for the Series tab. Titles are used directly in the UI.
 */
enum class SeriesFilter(val title: String) {
    Joined("My Series"),
    Regionals("Regionals"),
    All("All Series");

    companion object {
        val Default: SeriesFilter = Regionals
    }
}

/**
 * Returns [this] filtered by [filter] and sorted by [sortedSeries] with filter-appropriate
 * priorities. See [sortedSeries] for the ordering rules.
 */
fun List<Series>.filteredAndSorted(filter: SeriesFilter): List<Series> = when (filter) {
    SeriesFilter.Joined -> filter { it.isJoined }.sortedSeries(prioritizeRecent = true)
    SeriesFilter.Regionals -> filter { it.isRegional }.sortedSeries(prioritizeJoined = true)
    SeriesFilter.All -> sortedSeries(prioritizeRecent = true)
}

/**
 * Sort order (highest priority first):
 *   1. Joined series first      — only when [prioritizeJoined] is true
 *   2. Ended series last        — endDate strictly before now
 *   3. Empty series before ended — pilotCount == 0
 *   4. Most recent year first   — only when [prioritizeRecent] is true
 *   5. Higher pilot count first — tie-breaker
 */
internal fun List<Series>.sortedSeries(
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

    return sortedWith(Comparator { a, b ->
        if (prioritizeJoined && a.isJoined != b.isJoined) {
            return@Comparator if (a.isJoined) -1 else 1
        }

        val aEnded = a.endDate?.let { it.time < now } ?: false
        val bEnded = b.endDate?.let { it.time < now } ?: false
        if (aEnded != bEnded) return@Comparator if (aEnded) 1 else -1

        val aEmpty = a.pilotCount == 0
        val bEmpty = b.pilotCount == 0
        if (aEmpty != bEmpty) return@Comparator if (aEmpty) 1 else -1

        if (prioritizeRecent) {
            val ay = year(a)
            val by = year(b)
            if (ay != null && by != null && ay != by) return@Comparator by.compareTo(ay)
            if ((ay == null) != (by == null)) return@Comparator if (ay == null) 1 else -1
        }

        b.pilotCount.compareTo(a.pilotCount)
    })
}
