package com.multigp.racesync.screens.standings

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.util.TypedValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.domain.model.Standing
import com.multigp.racesync.domain.model.StandingSeason
import com.multigp.racesync.viewmodels.StandingsViewModel
import com.multigp.racesync.viewmodels.UiState
import java.io.File
import java.io.FileOutputStream

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

    var showBadgeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(season) {
        viewModel.fetchStandings(season)
    }

    // Determine if user's row is visible in the list
    val isMyRowVisible by remember {
        derivedStateOf {
            if (myStanding == null) return@derivedStateOf true
            val standings = (standingsUiState as? UiState.Success)?.data ?: return@derivedStateOf true
            val myIndex = standings.indexOfFirst { it.userId == myUserId }
            if (myIndex < 0) return@derivedStateOf true
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            visibleItems.any { it.index == myIndex }
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
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChanged = { viewModel.onSearchQueryChanged(it) }
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Content
            when (val state = standingsUiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Could not load the season standings.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Please try again later.",
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
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.length >= 2) "No Results" else "No standings available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Section header
                        SectionHeader(
                            count = standings.size,
                            isFiltered = searchQuery.length >= 2
                        )

                        // Main standings list
                        Box(modifier = Modifier.weight(1f)) {
                            StandingsList(
                                standings = standings,
                                listState = listState,
                                myUserId = myUserId,
                                onMyRowClicked = { showBadgeDialog = true }
                            )

                            // Pinned user row - shows when their row scrolls out of view
                            val standing = myStanding
                            if (standing != null && !isMyRowVisible && searchQuery.length < 2) {
                                PinnedUserRow(
                                    standing = standing,
                                    modifier = Modifier.align(Alignment.BottomCenter),
                                    onClick = { showBadgeDialog = true }
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
                onDismiss = { showBadgeDialog = false },
                onShare = {
                    shareBadgeImage(context, standing, season)
                    showBadgeDialog = false
                }
            )
        }
    }
}

@Composable
private fun StandingsTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {}
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Box(modifier = modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier.align(Alignment.Center)
            )
            IconButton(
                modifier = modifier.align(Alignment.CenterStart),
                onClick = onGoBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    modifier: Modifier = Modifier,
    onQueryChanged: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = {
            Text(
                text = "Filter pilots",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun SectionHeader(
    count: Int,
    isFiltered: Boolean,
    modifier: Modifier = Modifier
) {
    val headerText = if (isFiltered) {
        "$count results"
    } else {
        "Fastest 3 Consecutive Laps  \u2022  $count pilots"
    }

    Text(
        text = headerText,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun StandingsList(
    standings: List<Standing>,
    listState: LazyListState,
    myUserId: String?,
    modifier: Modifier = Modifier,
    onMyRowClicked: () -> Unit = {}
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = standings,
            key = { _, standing -> "${standing.position}_${standing.userId}" }
        ) { index, standing ->
            val isCurrentUser = myUserId != null && standing.userId == myUserId
            StandingRow(
                standing = standing,
                isAlternateRow = index % 2 == 1,
                isCurrentUser = isCurrentUser,
                onClick = if (isCurrentUser) onMyRowClicked else null
            )
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun StandingRow(
    standing: Standing,
    isAlternateRow: Boolean = false,
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = when {
        isCurrentUser -> MaterialTheme.colorScheme.primaryContainer
        isAlternateRow -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val subtitleColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank badge
        RankBadge(
            rank = standing.position,
            modifier = Modifier.width(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Name and scores
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = standing.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
            Text(
                text = standing.subtitleLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = subtitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Share icon for current user
        if (isCurrentUser) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share standing",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onClick?.invoke() }
            )
        }
    }
}

@Composable
private fun PinnedUserRow(
    standing: Standing,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RankBadge(
                rank = standing.position,
                modifier = Modifier.width(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = standing.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = standing.subtitleLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share standing",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RankBadge(
    rank: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (rank) {
            1 -> Text(text = "\uD83E\uDD47", fontSize = 24.sp) // gold
            2 -> Text(text = "\uD83E\uDD48", fontSize = 24.sp) // silver
            3 -> Text(text = "\uD83E\uDD49", fontSize = 24.sp) // bronze
            else -> Text(
                text = rank.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// Standing Badge Dialog & Share
// ============================================================

@Composable
private fun StandingBadgeDialog(
    standing: Standing,
    season: StandingSeason,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Badge preview
                StandingBadgePreview(standing = standing, season = season)

                Spacer(modifier = Modifier.height(20.dp))

                // Share button
                androidx.compose.material3.Button(
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share My Standing")
                }

                Spacer(modifier = Modifier.height(8.dp))

                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun StandingBadgePreview(
    standing: Standing,
    season: StandingSeason,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFF1A1A2E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rank
            Text(
                text = standing.positionWithSuffix,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Name with flag
            Text(
                text = standing.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = "Fastest 3 Consecutive Laps",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scores
            if (standing.score1Label.isNotEmpty()) {
                BadgeScoreRow(label = standing.score1Label)
            }
            if (standing.score2Label.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                BadgeScoreRow(label = standing.score2Label)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Season tag
            Text(
                text = season.title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun BadgeScoreRow(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFFFD700) // Gold/yellow for scores
    )
}

// ============================================================
// Share Badge as Image
// ============================================================

private fun shareBadgeImage(context: Context, standing: Standing, season: StandingSeason) {
    val bitmap = renderBadgeBitmap(context, standing, season)

    // Save bitmap to cache directory
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, "standing_badge.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "${standing.displayName} - ${standing.positionWithSuffix} in ${season.title}")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share Standing"))
}

private fun renderBadgeBitmap(context: Context, standing: Standing, season: StandingSeason): Bitmap {
    val width = 540
    val height = 400
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val density = context.resources.displayMetrics.density

    // Background gradient
    val bgPaint = Paint().apply {
        shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            AndroidColor.parseColor("#1A1A2E"),
            AndroidColor.parseColor("#16213E"),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Rank text
    val rankPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 72f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(standing.positionWithSuffix, width / 2f, 90f, rankPaint)

    // Checkered flag emoji + Name
    val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 28f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(standing.displayName, width / 2f, 135f, namePaint)

    // Subtitle
    val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#AAAAAA")
        textSize = 20f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("Fastest 3 Consecutive Laps", width / 2f, 170f, subtitlePaint)

    // Divider line
    val dividerPaint = Paint().apply {
        color = AndroidColor.parseColor("#333355")
        strokeWidth = 2f
    }
    canvas.drawLine(40f, 195f, width - 40f, 195f, dividerPaint)

    // Score labels in yellow
    val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#FFD700")
        textSize = 26f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    if (standing.score1Label.isNotEmpty()) {
        canvas.drawText(standing.score1Label, width / 2f, 240f, scorePaint)
    }
    if (standing.score2Label.isNotEmpty()) {
        canvas.drawText(standing.score2Label, width / 2f, 280f, scorePaint)
    }

    // Season title at bottom
    val seasonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#666688")
        textSize = 18f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(season.title, width / 2f, 340f, seasonPaint)

    // MultiGP branding
    val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#888888")
        textSize = 14f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("MultiGP Global Qualifier", width / 2f, 375f, brandPaint)

    return bitmap
}
