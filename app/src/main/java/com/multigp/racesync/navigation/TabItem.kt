package com.multigp.racesync.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.multigp.racesync.R

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(
    @DrawableRes var iconDefault: Int,
    @DrawableRes var iconSelected: Int,
    @StringRes var title: Int,
    var screen: ComposableFun? = null
) {
    object Joined : TabItem(
        R.drawable.ic_tab_join_outlined,
        R.drawable.ic_tab_join,
        R.string.tab_title_joined_races,
        null
    )

    object Nearby :
        TabItem(
            R.drawable.ic_tab_nearby_outlined,
            R.drawable.ic_tab_nearby,
            R.string.tab_title_nearby_races,
            null
        )

    object Chapters :
        TabItem(
            R.drawable.ic_tab_chapter_outlined,
            R.drawable.ic_tab_chapter,
            R.string.tab_title_chapters,
            null
        )

    object RaceDetails :
        TabItem(
            R.drawable.ic_bottom_bar_details,
            R.drawable.ic_bottom_bar_details,
            R.string.tab_title_race_details,
            null
        )

    object RaceRoster :
        TabItem(
            R.drawable.ic_bottom_roster,
            R.drawable.ic_bottom_roster,
            R.string.tab_title_race_roster,
            null
        )

    object PilotRace : TabItem(
        R.drawable.ic_pilot_race_outlined,
        R.drawable.ic_pilot_race,
        R.string.tab_title_pilot_races,
        null
    )

}

val landingTabs = listOf(TabItem.Joined, TabItem.Nearby, TabItem.Chapters)
val raceDetailTabs = listOf(TabItem.RaceDetails, TabItem.RaceRoster)
val pilotInfoTabs = listOf(TabItem.PilotRace, TabItem.Chapters)