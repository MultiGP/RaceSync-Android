package com.multigp.racesync.domain.model.io

/**
 * High-level filter the user can apply to the IO schedule. Combines the two orthogonal axes
 * the iOS app exposes (`All / MySchedule`) with derived race-series buckets built by
 * keyword-matching the free-text `activity` field of each session.
 *
 * Sessions whose activity doesn't match any series bucket (Movie Night, Kung Fu Fightn,
 * 50+ Vintage, etc.) are only visible under [All] — they aren't bucketed into a "Special"
 * category, by product decision.
 */
enum class EventActivityCategory(val title: String) {
    All("All"),
    MySchedule("My Schedule"),
    WorldCup("World Cup"),
    OpenFly("Open Fly"),
    Spec("Spec"),
    ClassRaces("Class Races"),
    GlobalQualifier("GQ"),
    Whoop("Whoop"),
    TeamRace("Team Race");

    companion object {
        fun fromTitle(title: String?): EventActivityCategory =
            entries.firstOrNull { it.title.equals(title, ignoreCase = true) } ?: All
    }
}

/**
 * Bucket a raw `activity` label into an [EventActivityCategory], or `null` if it doesn't
 * match any series bucket (one-off / themed sessions). Keyword matching is intentionally
 * lenient to absorb typos seen in the upstream feed (e.g. "Global Qualifer", "Burn Motors").
 *
 * Order matters: [EventActivityCategory.OpenFly] is checked first so "Open Fly" doesn't
 * fall through to broader matchers.
 */
fun categorizeActivity(activity: String?): EventActivityCategory? {
    val a = activity?.lowercase()?.trim().orEmpty()
    if (a.isEmpty()) return null
    return when {
        a.contains("open fly") || a.contains("openfly") -> EventActivityCategory.OpenFly
        a.contains("world cup") -> EventActivityCategory.WorldCup
        a.contains("whoop") -> EventActivityCategory.Whoop
        // "Global Qualifier" + the upstream typo "Global Qualifer".
        a.contains("global qualif") -> EventActivityCategory.GlobalQualifier
        a.contains("team race") -> EventActivityCategory.TeamRace
        // Pro Spec, IO Spec, Freedom Spec, AER (American Endurance Racing) — iOS groups AER here.
        a.contains("spec") || a.contains("aer") -> EventActivityCategory.Spec
        // Themed class races, plus the "Burn Motors" typo.
        a.contains("lipo") ||
            a.contains("voltage") ||
            a.contains("hammers") ||
            a.contains("cracked props") ||
            a.contains("burnt motors") ||
            a.contains("burn motors") -> EventActivityCategory.ClassRaces
        else -> null
    }
}
