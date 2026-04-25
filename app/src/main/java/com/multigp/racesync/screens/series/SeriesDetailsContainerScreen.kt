package com.multigp.racesync.screens.series

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomAlertDialog
import com.multigp.racesync.composables.JoinRaceUI
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.ResignRaceUI
import com.multigp.racesync.composables.bottombars.RaceDetailsBottomBar
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceApprovalState
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.model.SeriesResult
import com.multigp.racesync.domain.model.approvalState
import com.multigp.racesync.navigation.seriesDetailTabs
import com.multigp.racesync.viewmodels.SeriesDetailsViewModel
import com.multigp.racesync.viewmodels.UiState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SeriesDetailsContainerScreen(
    seriesId: String,
    modifier: Modifier = Modifier,
    viewModel: SeriesDetailsViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {},
    onRaceSelected: (Race) -> Unit = {},
    onPilotSelected: (SeriesResult) -> Unit = {},
    onChapterSelected: (SeriesResult) -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val state by viewModel.state.collectAsState()
    val joinRaceUiState by viewModel.joinRaceUiState.collectAsState()
    val resignRaceUiState by viewModel.resignRaceUiState.collectAsState()
    val approverActionUiState by viewModel.approverActionUiState.collectAsState()
    val loadingRaceId by viewModel.loadingRaceId.collectAsState()
    val myUserId by viewModel.myUserId.collectAsState()

    var pendingResignRace by remember { mutableStateOf<Race?>(null) }
    var pendingUnapproveRace by remember { mutableStateOf<Race?>(null) }
    var pendingRemoveRace by remember { mutableStateOf<Race?>(null) }

    LaunchedEffect(seriesId) {
        viewModel.load(seriesId)
    }

    val handleJoinRace: (Race) -> Unit = { race ->
        if (race.isJoined) {
            pendingResignRace = race
        } else {
            viewModel.joinRace(race.id)
        }
    }

    // Approver actions: Approved → confirm before unapproving; NotApproved → approve directly.
    val handleApproveRace: (Race) -> Unit = { race ->
        when (race.approvalState()) {
            RaceApprovalState.NotApproved -> viewModel.approveRace(race.id)
            RaceApprovalState.Approved -> pendingUnapproveRace = race
            RaceApprovalState.Completed -> Unit
        }
    }

    val handleRemoveRace: (Race) -> Unit = { race ->
        pendingRemoveRace = race
    }

    Scaffold(
        topBar = {
            RaceDetailsTopBar(
                title = stringResource(R.string.series_details_title),
                onGoBack = onGoBack
            )
        },
        bottomBar = {
            RaceDetailsBottomBar(seriesDetailTabs, pagerState, modifier = modifier)
        }
    ) { paddingValues ->
        when (val current = state) {
            is UiState.Loading,
            is UiState.None -> ProgressHUD(
                modifier = modifier,
                text = R.string.series_details_loading
            )

            is UiState.Error -> PlaceholderScreen(
                modifier = modifier,
                title = stringResource(R.string.series_details_error_title),
                message = current.message,
                buttonTitle = stringResource(R.string.error_btn_title_retry),
                isError = true,
                canRetry = true,
                onButtonClick = { viewModel.load(seriesId) }
            )

            is UiState.Success -> SeriesDetailsPager(
                series = current.data,
                pagerState = pagerState,
                myUserId = myUserId,
                loadingRaceId = loadingRaceId,
                onRaceSelected = onRaceSelected,
                onJoinRace = handleJoinRace,
                onApproveRace = handleApproveRace,
                onRemoveRace = handleRemoveRace,
                onPilotSelected = onPilotSelected,
                onChapterSelected = onChapterSelected,
                modifier = modifier.padding(paddingValues)
            )
        }
    }

    JoinRaceUI(
        uiState = joinRaceUiState,
        modifier = modifier,
        onProcessComplete = { viewModel.acknowledgeJoinRaceUi() }
    )

    ResignRaceUI(
        uiState = resignRaceUiState,
        modifier = modifier,
        onProcessComplete = { viewModel.acknowledgeResignRaceUi() }
    )

    ApproverActionUi(
        uiState = approverActionUiState,
        onAcknowledge = { viewModel.acknowledgeApproverActionUi() }
    )

    pendingResignRace?.let { race ->
        CustomAlertDialog(
            title = stringResource(R.string.alert_resign_race_title),
            body = stringResource(R.string.alert_resign_race_message),
            confirmButtonTitle = stringResource(R.string.alert_resign_race_lbl_btn_confirm),
            dismissButtonTitle = stringResource(R.string.lbl_btn_cancel),
            onConfirm = {
                viewModel.resignFromRace(race.id)
                pendingResignRace = null
            },
            onDismiss = { pendingResignRace = null },
            onDismissRequest = { pendingResignRace = null }
        )
    }

    pendingUnapproveRace?.let { race ->
        CustomAlertDialog(
            title = stringResource(R.string.alert_unapprove_race_title),
            body = stringResource(R.string.alert_unapprove_race_message),
            confirmButtonTitle = stringResource(R.string.alert_unapprove_race_confirm),
            dismissButtonTitle = stringResource(R.string.lbl_btn_cancel),
            onConfirm = {
                viewModel.unapproveRace(race.id)
                pendingUnapproveRace = null
            },
            onDismiss = { pendingUnapproveRace = null },
            onDismissRequest = { pendingUnapproveRace = null }
        )
    }

    pendingRemoveRace?.let { race ->
        CustomAlertDialog(
            title = stringResource(R.string.alert_remove_race_title),
            body = stringResource(R.string.alert_remove_race_message),
            confirmButtonTitle = stringResource(R.string.alert_remove_race_confirm),
            dismissButtonTitle = stringResource(R.string.lbl_btn_cancel),
            onConfirm = {
                viewModel.removeRaceFromSeries(race.id)
                pendingRemoveRace = null
            },
            onDismiss = { pendingRemoveRace = null },
            onDismissRequest = { pendingRemoveRace = null }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SeriesDetailsPager(
    series: Series,
    pagerState: com.google.accompanist.pager.PagerState,
    myUserId: String?,
    loadingRaceId: String?,
    onRaceSelected: (Race) -> Unit,
    onJoinRace: (Race) -> Unit,
    onApproveRace: (Race) -> Unit,
    onRemoveRace: (Race) -> Unit,
    onPilotSelected: (SeriesResult) -> Unit,
    onChapterSelected: (SeriesResult) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        count = seriesDetailTabs.size,
        itemSpacing = 16.dp,
        modifier = modifier
    ) { page ->
        when (page) {
            0 -> SeriesDetailsTab(series = series)
            1 -> SeriesRacesTab(
                series = series,
                myUserId = myUserId,
                loadingRaceId = loadingRaceId,
                onRaceSelected = onRaceSelected,
                onJoinRace = onJoinRace,
                onApproveRace = onApproveRace,
                onRemoveRace = onRemoveRace
            )
            2 -> SeriesLeaderboardTab(
                series = series,
                onPilotSelected = onPilotSelected,
                onChapterSelected = onChapterSelected
            )
        }
    }
}

/** Surfaces approve/unapprove/remove errors as a dismissible dialog. Success is silent. */
@Composable
private fun ApproverActionUi(uiState: UiState<Unit>, onAcknowledge: () -> Unit) {
    when (uiState) {
        is UiState.Error -> CustomAlertDialog(
            title = stringResource(R.string.approver_action_error_title),
            body = uiState.message,
            confirmButtonTitle = stringResource(R.string.ok),
            onConfirm = onAcknowledge,
            onDismiss = onAcknowledge,
            onDismissRequest = onAcknowledge
        )
        is UiState.Success -> {
            // Refresh already handled in the ViewModel; just clear the flag.
            LaunchedEffect(uiState) { onAcknowledge() }
        }
        else -> Unit
    }
}
