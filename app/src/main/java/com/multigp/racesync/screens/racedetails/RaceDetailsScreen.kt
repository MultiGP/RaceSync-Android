package com.multigp.racesync.screens.racedetails

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomAlertDialog
import com.multigp.racesync.composables.CustomMap
import com.multigp.racesync.composables.JoinRaceUI
import com.multigp.racesync.composables.ResignRaceUI
import com.multigp.racesync.composables.buttons.JoinButton
import com.multigp.racesync.composables.buttons.ParticipantsButton
import com.multigp.racesync.composables.cells.RaceDetailsCell
import com.multigp.racesync.composables.text.HtmlText
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun RaceDetailsScreen(
    data: Triple<Profile, Race, RaceView>,
    modifier: Modifier = Modifier,
    joinRaceUiState: UiState<Boolean>,
    resignRaceUiState: UiState<Boolean>,
    viewModel: LandingViewModel = hiltViewModel()
) {
    var showResignRaceDialog by remember { mutableStateOf(false) }
    var showRaceMap by remember { mutableStateOf(false) }
    var showMapOptionsSheet by remember { mutableStateOf(false) }
    var showZippyQ by remember { mutableStateOf(false) }
    var selectedRace by remember { mutableStateOf<Race?>(null) }

    val (profile, race, raceView) = data

    // Local UI state for instant join/resign feedback
    var isJoined by remember { mutableStateOf(race.isJoined) }
    var participantCount by remember { mutableIntStateOf(race.participantCount) }
    val loadingRaceId by viewModel.loadingRaceId.collectAsState()
    val isLoading = loadingRaceId == race.id

    // React to successful join — update local state immediately
    if (joinRaceUiState is UiState.Success && !isJoined) {
        isJoined = true
        participantCount += 1
    }
    // React to successful resign — update local state immediately
    if (resignRaceUiState is UiState.Success && isJoined) {
        isJoined = false
        participantCount -= 1
    }

    val onJoinRace: (Race) -> Unit = { raceToJoin ->
        selectedRace = raceToJoin
        if (!isJoined) {
            viewModel.joinRace(raceToJoin.id)
        } else {
            showResignRaceDialog = true
        }
    }

    RaceContentsScreen(
        race,
        raceView = raceView,
        modifier = modifier,
        isJoined = isJoined,
        isLoading = isLoading,
        participantCount = participantCount,
        onJoinRace = onJoinRace,
        onShowMap = { showRaceMap = true },
        onZippyQClick = { showZippyQ = true }
    )

    // In-app ZippyQ WebView (full-screen dialog)
    if (showZippyQ) {
        ZippyQDialog(
            raceId = race.id,
            onDismiss = { showZippyQ = false }
        )
    }

    if (showRaceMap) {
        MapsBottomSheet(
            race = race,
            modifier = modifier,
            onSheetDissmissed = { showRaceMap = false },
            onSelectMapOption = {
                showMapOptionsSheet = true
            }
        )
    }

    if (showMapOptionsSheet) {
        MapOptionsBottomSheet(
            race = race,
            modifier = modifier,
            onSheetDissmissed = {
                showMapOptionsSheet = false
            },
        )
    }

    JoinRaceUI(
        uiState = joinRaceUiState,
        modifier = modifier,
        onProcessComplete = { viewModel.updateJoinRaceUiState(true) })

    if (showResignRaceDialog) {
        CustomAlertDialog(
            title = stringResource(R.string.alert_resign_race_title),
            body = stringResource(R.string.alert_resign_race_message),
            confirmButtonTitle = stringResource(R.string.alert_resign_race_lbl_btn_confirm),
            dismissButtonTitle = stringResource(R.string.lbl_btn_cancel),
            onConfirm = {
                viewModel.resignFromRace((selectedRace?.id)!!)
                showResignRaceDialog = false
            },
            onDismiss = {
                showResignRaceDialog = false
            },
            onDismissRequest = {
                showResignRaceDialog = false
            }
        )
    }

    ResignRaceUI(
        uiState = resignRaceUiState,
        modifier = modifier,
        onProcessComplete = { viewModel.updateResignRaceUiState(true) })
}

/**
 * Maps the numeric race class ID to the corresponding badge drawable resource.
 * Matches iOS RaceViewModel.raceClassImage() → badge_class_[name].
 */
fun raceClassBadgeRes(raceClass: String?): Int? {
    return when (raceClass) {
        "0" -> R.drawable.badge_class_open
        "1" -> R.drawable.badge_class_whoop
        "2" -> R.drawable.badge_class_micro
        "3" -> R.drawable.badge_class_freedom
        "4" -> R.drawable.badge_class_spec7in
        "6" -> R.drawable.badge_class_esport
        "7" -> R.drawable.badge_class_spec5in
        "8" -> R.drawable.badge_class_prospec
        else -> null
    }
}

@Composable
fun RaceDetailsActions(
    race: Race,
    raceView: RaceView? = null,
    modifier: Modifier = Modifier,
    onZippyQClick: () -> Unit = {}
) {
    Column(modifier = modifier.padding(top = 8.dp)) {
        HorizontalDivider(color = Color.LightGray)
        RaceDetailsCell(
            label = "Class",
            value = race.raceClassString ?: "—",
            badgeImageRes = raceClassBadgeRes(race.raceClass)
        )
        HorizontalDivider(color = Color.LightGray)
        RaceDetailsCell("Coordinator", race.ownerUserName ?: "—")
        HorizontalDivider(color = Color.LightGray)
        RaceDetailsCell("Chapter", race.chapterName ?: "—")
        HorizontalDivider(color = Color.LightGray)
        RaceDetailsCell("Season", race.seasonName ?: "—")
        HorizontalDivider(color = Color.LightGray)
        // ZippyQ row — only shown when the race has ZippyQ enabled (matches iOS)
        if (raceView?.isZippyQEnabled == true) {
            RaceDetailsCell(
                label = "ZippyQ",
                value = "multigp.com",
                onClick = onZippyQClick
            )
            HorizontalDivider(color = Color.LightGray)
        }
    }
}

@Composable
fun RaceContentsScreen(
    race: Race,
    raceView: RaceView? = null,
    modifier: Modifier = Modifier,
    isJoined: Boolean = race.isJoined,
    isLoading: Boolean = false,
    participantCount: Int = race.participantCount,
    onJoinRace: (Race) -> Unit = {},
    onShowMap: () -> Unit = {},
    onZippyQClick: () -> Unit = {},
) {
    val state = rememberScrollState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        modifier = modifier.verticalScroll(state)
    ) {
        // Map — 1/3 screen height (matches iOS UIScreen.main.bounds.height / 3)
        CustomMap(
            location = race.location,
            markerTitle = race.name ?: "Unknown Race",
            markerSnippet = race.snippet,
            modifier = Modifier.height(screenHeight / 3),
            onMapClick = { onShowMap() }
        )
        Column(modifier = Modifier.padding(16.dp)) {
            // Race class subtitle — above title (matches iOS subtitleLabel)
            Text(
                text = (race.raceClassString ?: "").uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = Color.Gray
            )

            // Title row with trophy for GQ/official races
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (race.officialStatus == 2) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp, top = 4.dp),
                        painter = painterResource(R.drawable.ic_tropy),
                        contentDescription = null
                    )
                }
                Text(
                    text = (race.name ?: "Unknown Race").uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 2
                )
            }

            // Date/Location + Join/Participants row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                // Left: dates + location (constrained to not overlap join button)
                Column(modifier = Modifier.weight(1.0f)) {
                    IconText(
                        text = race.formatStartDateCompact(),
                        icon = R.drawable.ic_calendar_small
                    )
                    if (race.canDisplayEndDate) {
                        IconText(
                            modifier = Modifier.padding(top = 10.dp),
                            text = race.formatEndDateCompact(),
                            icon = R.drawable.ic_clock_small
                        )
                    }
                    IconText(
                        modifier = Modifier.padding(top = 10.dp),
                        text = race.getFormattedAddress(),
                        icon = R.drawable.ic_map_pin,
                        maxLines = 3,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onShowMap() }
                    )
                }
                // Right: join + participants (top-aligned, matches iOS rightStackView)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top
                ) {
                    JoinButton(
                        isJoined = isJoined,
                        status = race.status,
                        isLoading = isLoading,
                        onClick = { onJoinRace(race) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ParticipantsButton(text = "$participantCount", onClick = {})
                }
            }

            // Chapter name
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = race.chapterName.uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            // HTML description
            HtmlText(
                modifier = Modifier.padding(top = 8.dp),
                html = race.content ?: ""
            )

            // Itinerary section (if available)
            raceView?.let { rv ->
                if (rv.itineraryContent.isNotBlank()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                        color = Color.LightGray
                    )
                    HtmlText(
                        modifier = Modifier.padding(top = 4.dp),
                        html = rv.itineraryContent
                    )
                }
            }

            // Bottom detail cells
            RaceDetailsActions(
                race = race,
                raceView = raceView,
                onZippyQClick = onZippyQClick
            )
        }
    }
}

/**
 * Two-phase ZippyQ flow:
 * 1. Prompt user for MultiGP email & password
 * 2. Open a full-screen WebView that auto-logs in, then redirects to ZippyQ
 */
@Composable
fun ZippyQDialog(
    raceId: String,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var credentialsReady by remember { mutableStateOf(false) }

    if (!credentialsReady) {
        // Phase 1: credential prompt
        ZippyQLoginPrompt(
            email = email,
            password = password,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onConfirm = { credentialsReady = true },
            onDismiss = onDismiss
        )
    } else {
        // Phase 2: auto-login WebView
        ZippyQWebViewDialog(
            raceId = raceId,
            email = email,
            password = password,
            onDismiss = onDismiss
        )
    }
}

/**
 * Simple dialog asking for the user's MultiGP email and password.
 * These credentials are only held in memory for the WebView login — not persisted.
 */
@Composable
private fun ZippyQLoginPrompt(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ZippyQ Login") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Enter your MultiGP credentials to access ZippyQ.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Full-screen WebView dialog that:
 * 1. Loads the MultiGP login page
 * 2. Injects JS to fill the email/password fields and submit the form
 * 3. After successful login (redirect), navigates to the ZippyQ page
 *
 * The user never sees the login page — they see a loading spinner until ZippyQ loads.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZippyQWebViewDialog(
    raceId: String,
    email: String,
    password: String,
    onDismiss: () -> Unit
) {
    val zippyQUrl = "https://www.multigp.com/MultiGP/views/zippyq.php?raceId=$raceId"
    val loginUrl = "https://www.multigp.com/login/"
    var isLoggingIn by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ZippyQ") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Escape credentials for safe JS injection
            val safeEmail = remember(email) {
                email.replace("\\", "\\\\").replace("'", "\\'")
            }
            val safePassword = remember(password) {
                password.replace("\\", "\\\\").replace("'", "\\'")
            }

            if (isLoggingIn) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Signing in…", style = MaterialTheme.typography.bodyLarge)
                }
            }

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webChromeClient = WebChromeClient()

                        webViewClient = object : WebViewClient() {
                            private var loginSubmitted = false

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                val currentUrl = url ?: return

                                if (!loginSubmitted && currentUrl.contains("/login")) {
                                    // Login page loaded → fill form and submit
                                    loginSubmitted = true
                                    view?.evaluateJavascript("""
                                        (function() {
                                            setTimeout(function() {
                                                var emailField = document.getElementById('LoginForm_username');
                                                var passField = document.getElementById('LoginForm_password');
                                                if (emailField && passField) {
                                                    // Set values using native setter to trigger framework bindings
                                                    var nativeInputValueSetter = Object.getOwnPropertyDescriptor(
                                                        window.HTMLInputElement.prototype, 'value').set;
                                                    nativeInputValueSetter.call(emailField, '$safeEmail');
                                                    nativeInputValueSetter.call(passField, '$safePassword');

                                                    // Dispatch events so Yii's client validation recognizes the input
                                                    ['input', 'change', 'blur'].forEach(function(evt) {
                                                        emailField.dispatchEvent(new Event(evt, {bubbles: true}));
                                                        passField.dispatchEvent(new Event(evt, {bubbles: true}));
                                                    });

                                                    // Submit after a tick to let validation complete
                                                    setTimeout(function() {
                                                        var form = document.getElementById('login-form');
                                                        if (form) { form.submit(); }
                                                    }, 300);
                                                }
                                            }, 500);
                                        })();
                                    """.trimIndent(), null)
                                } else if (loginSubmitted && currentUrl.contains("/login")) {
                                    // Login failed (redirected back to login, e.g. ?s=f)
                                    // Show the page so user can see the error / retry manually
                                    isLoggingIn = false
                                } else if (!currentUrl.contains("/login")) {
                                    // Login succeeded (redirected away from login page)
                                    // Now navigate to ZippyQ
                                    if (!currentUrl.contains("zippyq.php")) {
                                        view?.loadUrl(zippyQUrl)
                                    }
                                    isLoggingIn = false
                                }
                            }
                        }

                        // Start by loading the login page
                        loadUrl(loginUrl)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun RaceDetailsScreenPreview() {
    RaceSyncTheme {
        RaceContentsScreen(race = Race.testObject)
    }
}