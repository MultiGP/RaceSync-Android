package com.multigp.racesync.screens.standings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.StandingSeason
import com.multigp.racesync.viewmodels.StandingsViewModel
import com.multigp.racesync.viewmodels.UiState
import kotlinx.coroutines.launch

@Composable
fun StandingsScreen(
    season: StandingSeason,
    modifier: Modifier = Modifier,
    viewModel: StandingsViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {}
) {
    val standingsUiState by viewModel.standingsUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val myUserId by viewModel.myUserId.collectAsState()
    val myStanding by viewModel.myStanding.collectAsState()
    val myProfilePictureUrl by viewModel.myProfilePictureUrl.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showBadgeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(season) {
        viewModel.fetchStandings(season)
    }

    // Track where the user's row is relative to the visible viewport
    // VISIBLE = on screen, ABOVE = scrolled off the top, BELOW = below the viewport
    val myRowPosition by remember {
        derivedStateOf {
            if (myStanding == null) return@derivedStateOf MyRowPosition.VISIBLE
            val standings = (standingsUiState as? UiState.Success)?.data ?: return@derivedStateOf MyRowPosition.VISIBLE
            val myIndex = standings.indexOfFirst { it.userId == myUserId }
            if (myIndex < 0) return@derivedStateOf MyRowPosition.VISIBLE

            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.any { it.index == myIndex }) return@derivedStateOf MyRowPosition.VISIBLE

            val firstVisible = visibleItems.firstOrNull()?.index ?: return@derivedStateOf MyRowPosition.BELOW
            if (myIndex < firstVisible) MyRowPosition.ABOVE else MyRowPosition.BELOW
        }
    }

    Scaffold(
        topBar = {
            StandingsTopBar(
                title = season.shortTitle,
                onGoBack = onGoBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChanged = { viewModel.onSearchQueryChanged(it) }
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            when (val state = standingsUiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.standings_error_title),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.standings_error_retry),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is UiState.Success -> {
                    val standings = state.data
                    if (standings.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.length >= 2)
                                    stringResource(R.string.standings_no_results)
                                else
                                    stringResource(R.string.standings_no_standings),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        SectionHeader(
                            count = standings.size,
                            isFiltered = searchQuery.length >= 2
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            StandingsList(
                                standings = standings,
                                listState = listState,
                                myUserId = myUserId,
                                onShareClicked = { showBadgeDialog = true },
                                onPullToRefresh = { viewModel.refresh() }
                            )

                            val standing = myStanding
                            if (standing != null && myRowPosition != MyRowPosition.VISIBLE && searchQuery.length < 2) {
                                val pinAlignment = if (myRowPosition == MyRowPosition.ABOVE)
                                    Alignment.TopCenter else Alignment.BottomCenter
                                PinnedUserRow(
                                    standing = standing,
                                    modifier = Modifier.align(pinAlignment),
                                    onTap = {
                                        val myIndex = standings.indexOfFirst { it.userId == myUserId }
                                        if (myIndex >= 0) {
                                            scope.launch {
                                                listState.animateScrollToItem(myIndex)
                                            }
                                        }
                                    },
                                    onShare = { showBadgeDialog = true }
                                )
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    // Badge share dialog
    if (showBadgeDialog) {
        myStanding?.let { standing ->
            StandingBadgeDialog(
                standing = standing,
                season = season,
                profilePictureUrl = myProfilePictureUrl,
                onDismiss = { showBadgeDialog = false },
                onShare = {
                    scope.launch {
                        shareBadgeImage(context, standing, season, myProfilePictureUrl)
                    }
                    showBadgeDialog = false
                }
            )
        }
    }
}

/**
 * Tracks where the signed-in user's row is relative to the visible list viewport.
 */
private enum class MyRowPosition {
    /** Row is currently visible on screen */
    VISIBLE,
    /** Row has scrolled off the top of the viewport */
    ABOVE,
    /** Row is below the visible viewport */
    BELOW
}
