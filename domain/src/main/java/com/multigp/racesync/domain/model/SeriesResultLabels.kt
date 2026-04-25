package com.multigp.racesync.domain.model

import com.multigp.racesync.domain.extensions.formatLapTime
import java.util.Locale

/**
 * Display strings for one row on the Series leaderboard. Pure value object so it can be
 * computed off the main thread, snapshotted, and unit-tested without Compose.
 */
data class SeriesResultLabels(
    /** Subtitle line (e.g. `"Races: 5  |  Elo: 1200"`, a lap time, or [EMPTY_PLACEHOLDER]). */
    val subtitle: String,
    /** Right-side score pill (e.g. `"42 pts"`). Empty string when there's nothing to show. */
    val score: String
) {
    val hasScore: Boolean get() = score.isNotEmpty()

    companion object {
        const val EMPTY_PLACEHOLDER: String = "--"
        const val SUBTITLE_SEPARATOR: String = "  |  "
    }
}

/**
 * Renders [result] for a row on the leaderboard, formatted per the series'
 * [scoreType] (matches iOS `SeriesResultViewModel`).
 *
 * Score-type rules:
 *   - **Fastest3Laps** — subtitle is the formatted lap time; no score pill.
 *   - **Collegiate** — subtitle is the lap time (pilot) or `Best: [..]` array (chapter);
 *     score pill is the points value with up to 3 decimals (trailing zeroes stripped).
 *   - **Overall / ProSpec / Regionals (default)** — subtitle is `Races: N` (when known)
 *     plus `Elo: N` for pilots; score pill is `N pts` (or `1 pt`).
 */
fun seriesResultLabels(result: SeriesResult, scoreType: SeriesScoreType): SeriesResultLabels =
    when (scoreType) {
        SeriesScoreType.Fastest3Laps -> fastest3LapsLabels(result)
        SeriesScoreType.Collegiate -> collegiateLabels(result)
        SeriesScoreType.Overall,
        SeriesScoreType.ProSpec,
        SeriesScoreType.Regionals -> overallLabels(result)
    }

private fun fastest3LapsLabels(result: SeriesResult) = SeriesResultLabels(
    subtitle = formatLapTime(result.fastest3Laps) ?: SeriesResultLabels.EMPTY_PLACEHOLDER,
    score = ""
)

private fun collegiateLabels(result: SeriesResult): SeriesResultLabels {
    val parts = mutableListOf<String>()
    when (result.type) {
        SeriesResultType.Pilot -> formatLapTime(result.fastest3Laps)?.let(parts::add)
        SeriesResultType.Chapter -> result.bestResults?.takeIf { it.isNotEmpty() }?.let { values ->
            val list = values.joinToString(", ") { stripTrailingZero(it) }
            parts += "Best: [$list]"
        }
    }

    val subtitle = if (parts.isNotEmpty()) {
        parts.joinToString(SeriesResultLabels.SUBTITLE_SEPARATOR)
    } else {
        SeriesResultLabels.EMPTY_PLACEHOLDER
    }
    val score = if (result.score > 0) formatPoints(result.score) else ""
    return SeriesResultLabels(subtitle, score)
}

private fun overallLabels(result: SeriesResult): SeriesResultLabels {
    val parts = mutableListOf<String>()
    if (result.raceCount > 0) parts += "Races: ${result.raceCount}"
    if (result.type == SeriesResultType.Pilot) parts += "Elo: ${result.eloScore}"

    val unit = if (result.score == 1.0) "pt" else "pts"
    val score = String.format(Locale.US, "%.0f %s", result.score, unit)
    return SeriesResultLabels(
        subtitle = parts.joinToString(SeriesResultLabels.SUBTITLE_SEPARATOR),
        score = score
    )
}

/** Up to 3 decimals, with trailing zeros (and the dot) trimmed. */
private fun formatPoints(score: Double): String {
    val rounded = String.format(Locale.US, "%.3f", score)
    return rounded.trimEnd('0').trimEnd('.')
}

/** Drops a trailing `.0` when present so `5.0 → "5"` and `5.25 → "5.25"`. */
private fun stripTrailingZero(value: Double): String {
    val asLong = value.toLong()
    return if (value == asLong.toDouble()) asLong.toString() else value.toString()
}
