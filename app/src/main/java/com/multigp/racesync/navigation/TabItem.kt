package com.multigp.racesync.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.multigp.racesync.R
import com.multigp.racesync.screens.landing.ChaptersScreen
import com.multigp.racesync.screens.landing.JoinedRacesScreen
import com.multigp.racesync.screens.landing.NearbyRacesScreen

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(
    @DrawableRes var iconDefault: Int,
    @DrawableRes var iconSelected: Int,
    @StringRes var title: Int,
    var screen: ComposableFun
) {
    object Joined : TabItem(
        R.drawable.ic_tab_join_outlined,
        R.drawable.ic_tab_join,
        R.string.tab_title_joined_races,
        { JoinedRacesScreen() }
    )

    object Nearby :
        TabItem(
            R.drawable.ic_tab_nearby_outlined,
            R.drawable.ic_tab_nearby,
            R.string.tab_title_nearby_races,
            { NearbyRacesScreen() })

    object Chapters :
        TabItem(
            R.drawable.ic_tab_chapter_outlined,
            R.drawable.ic_tab_chapter,
            R.string.tab_title_chapters,
            { ChaptersScreen() })
}

val landingTabs = listOf(TabItem.Joined, TabItem.Nearby, TabItem.Chapters)