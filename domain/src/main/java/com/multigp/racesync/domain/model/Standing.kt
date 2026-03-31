package com.multigp.racesync.domain.model

data class Standing(
    val position: Int = 0,
    val firstName: String = "",
    val userName: String = "",
    val lastName: String = "",
    val userId: String = "",
    val chapterName: String = "",
    val country: String = "",
    val season1: String = "",
    val season1Score: Double = 0.0,
    val season2: String = "",
    val season2Score: Double = 0.0
) {
    val displayName: String
        get() {
            val flag = countryToFlag(country)
            return "$flag $firstName '$userName' $lastName"
        }

    val subtitleLabel: String
        get() {
            return if (season1 == "2023") {
                formatTime(season1Score)
            } else {
                val spring = "Spring: ${formatTime(season1Score)}"
                val summer = "Summer: ${formatTime(season2Score)}"
                "$spring  |  $summer"
            }
        }

    val score1Label: String
        get() {
            return if (season1 == "2023") {
                formatTime(season1Score)
            } else {
                "Spring: ${formatTime(season1Score)}"
            }
        }

    val score2Label: String
        get() {
            return if (season1 == "2023") "" else "Summer: ${formatTime(season2Score)}"
        }

    val positionWithSuffix: String
        get() = ordinalSuffix(position)

    companion object {
        fun ordinalSuffix(n: Int): String {
            val suffixes = arrayOf("th", "st", "nd", "rd")
            val mod100 = n % 100
            val suffix = if (mod100 in 11..13) "th"
            else suffixes.getOrElse(n % 10) { "th" }
            return "$n$suffix"
        }

        fun formatTime(value: Double): String {
            if (value <= 0 || value > 180) return "N/A"

            return if (value < 60) {
                val truncated = Math.floor(value * 1000) / 1000
                String.format("%.3fs", truncated)
            } else {
                val minutes = value.toInt() / 60
                val seconds = value % 60
                String.format("%d:%06.3f", minutes, seconds)
            }
        }

        fun countryToFlag(countryCode: String): String {
            if (countryCode.length != 2) return ""
            val first = Character.codePointAt(countryCode.uppercase(), 0) - 0x41 + 0x1F1E6
            val second = Character.codePointAt(countryCode.uppercase(), 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(first)) + String(Character.toChars(second))
        }
    }
}

enum class StandingSeason(val value: String) {
    Y2025("2025"),
    Y2024("2024"),
    Y2023("2023");

    val title: String get() = "$value MultiGP Global Qualifier"
    val shortTitle: String get() = "MultiGP GQ $value"
}
