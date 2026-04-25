package com.multigp.racesync.domain.model

/**
 * Mirrors the MultiGP `SeriesScore` enum. Drives row formatting on the leaderboard tab.
 * Unrecognised / missing values fall back to [Overall].
 */
enum class SeriesScoreType(val raw: String) {
    Overall("0"),
    Collegiate("1"),
    ProSpec("2"),
    Fastest3Laps("3"),
    Regionals("4");

    companion object {
        fun fromRaw(raw: String?): SeriesScoreType =
            entries.firstOrNull { it.raw == raw } ?: Overall
    }
}
