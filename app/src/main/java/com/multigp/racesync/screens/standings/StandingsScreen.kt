package com.multigp.racesync.screens.standings

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Standing
import com.multigp.racesync.domain.model.StandingSeason
import com.multigp.racesync.viewmodels.StandingsViewModel
import com.multigp.racesync.viewmodels.UiState
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

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
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
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

                        // Main standings list with pull-to-refresh
                        Box(modifier = Modifier.weight(1f)) {
                            StandingsList(
                                standings = standings,
                                listState = listState,
                                myUserId = myUserId,
                                onMyRowClicked = { showBadgeDialog = true },
                                onPullToRefresh = { viewModel.refresh() }
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
                profilePictureUrl = myProfilePictureUrl,
                onDismiss = { showBadgeDialog = false },
                onShare = {
                    shareBadgeImage(context, standing, season, myProfilePictureUrl)
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
    onMyRowClicked: () -> Unit = {},
    onPullToRefresh: () -> Unit = {}
) {
    val canScrollUp by remember { derivedStateOf { listState.canScrollBackward } }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    // If at the top of the list and dragging down
                    if (!canScrollUp && dragAmount > 80f) {
                        onPullToRefresh()
                    }
                }
            }
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
// Standing Badge Dialog & Share (iOS-matching design)
// ============================================================

private val BadgeDarkBlue = Color(0xFF232B5B)
private val BadgeYellow = Color(0xFFF9D749)

@Composable
private fun StandingBadgeDialog(
    standing: Standing,
    season: StandingSeason,
    profilePictureUrl: String?,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge card preview
            StandingBadgePreview(
                standing = standing,
                season = season,
                profilePictureUrl = profilePictureUrl
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Share button (iOS style: white rounded button)
            Button(
                onClick = onShare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share to...",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun StandingBadgePreview(
    standing: Standing,
    season: StandingSeason,
    profilePictureUrl: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = BadgeDarkBlue
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ---- Top section: Profile photo with gradient overlay ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Square photo area
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                // Profile photo
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profilePictureUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay (bottom to top, red-dark fading to transparent)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x33111133),
                                    Color(0x99111133),
                                    Color(0xCCCC0000)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Rank number overlay (bottom-left)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 16.dp)
                ) {
                    // MultiGP icon (replaces checkered flag)
                    Image(
                        painter = painterResource(id = R.drawable.icn_mgp_small_white),
                        contentDescription = "MultiGP",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = standing.positionWithSuffix,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 64.sp
                    )
                    Text(
                        text = "Global Rank",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // ---- Bottom section: Info panel (dark blue) ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BadgeDarkBlue)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Pilot name with flag
                Text(
                    text = standing.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle
                Text(
                    text = "Fastest 3 Consecutive Laps",
                    fontSize = 12.sp,
                    color = Color(0xFF6D6D77)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scores row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (standing.score1Label.isNotEmpty()) {
                            Text(
                                text = standing.score1Label,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BadgeYellow
                            )
                        }
                        if (standing.score2Label.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = standing.score2Label,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BadgeYellow
                            )
                        }
                    }

                    // GQ MultiGP logo (right side)
                    Image(
                        painter = painterResource(id = R.drawable.standing_badge_logo),
                        contentDescription = "GQ MultiGP",
                        modifier = Modifier
                            .height(50.dp)
                            .width(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

// ============================================================
// Share Badge as Image
// ============================================================

private fun shareBadgeImage(
    context: Context,
    standing: Standing,
    season: StandingSeason,
    profilePictureUrl: String?
) {
    // Launch on a background thread to download profile photo
    Thread {
        val profileBitmap = downloadProfileBitmap(profilePictureUrl)
        val bitmap = renderBadgeBitmap(context, standing, season, profileBitmap)

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
            putExtra(
                Intent.EXTRA_TEXT,
                "${standing.displayName} - ${standing.positionWithSuffix} in ${season.title}"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Must start activity on main thread
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            context.startActivity(Intent.createChooser(shareIntent, "Share Standing"))
        }
    }.start()
}

private fun downloadProfileBitmap(url: String?): Bitmap? {
    if (url.isNullOrBlank()) return null
    return try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.doInput = true
        connection.connect()
        val inputStream = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        connection.disconnect()
        bitmap
    } catch (e: Exception) {
        Log.e("StandingsBadge", "Failed to download profile photo", e)
        null
    }
}

private fun renderBadgeBitmap(
    context: Context,
    standing: Standing,
    season: StandingSeason,
    profileBitmap: Bitmap?
): Bitmap {
    val width = 540
    val photoHeight = 540
    val infoHeight = 180
    val totalHeight = photoHeight + infoHeight
    val bitmap = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // ---- Top section: Profile photo ----
    if (profileBitmap != null) {
        // Scale and center-crop the profile image to fill 540x540
        val srcW = profileBitmap.width
        val srcH = profileBitmap.height
        val scale = maxOf(width.toFloat() / srcW, photoHeight.toFloat() / srcH)
        val scaledW = (srcW * scale).toInt()
        val scaledH = (srcH * scale).toInt()
        val offsetX = (width - scaledW) / 2
        val offsetY = (photoHeight - scaledH) / 2
        val destRect = Rect(offsetX, offsetY, offsetX + scaledW, offsetY + scaledH)
        canvas.drawBitmap(profileBitmap, null, destRect, null)
    } else {
        // Fallback: dark gradient background if no photo
        val fallbackPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, photoHeight.toFloat(),
                AndroidColor.parseColor("#333355"),
                AndroidColor.parseColor("#1A1A2E"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), photoHeight.toFloat(), fallbackPaint)
    }

    // Gradient overlay on the photo (bottom to top, red-dark fading to transparent)
    val overlayPaint = Paint().apply {
        shader = LinearGradient(
            0f, 0f,
            0f, photoHeight.toFloat(),
            intArrayOf(
                AndroidColor.parseColor("#00000000"),  // Transparent at top
                AndroidColor.parseColor("#33111133"),
                AndroidColor.parseColor("#99111133"),
                AndroidColor.parseColor("#CCCC0000")   // Red at bottom
            ),
            floatArrayOf(0f, 0.4f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), photoHeight.toFloat(), overlayPaint)

    // MultiGP icon (replaces checkered flag)
    val mgpIcon = BitmapFactory.decodeResource(context.resources, R.drawable.icn_mgp_small_white)
    if (mgpIcon != null) {
        val iconSize = 40
        val iconDest = Rect(20, photoHeight - 145, 20 + iconSize, photoHeight - 145 + iconSize)
        canvas.drawBitmap(mgpIcon, null, iconDest, null)
    }

    // Rank number
    val rankPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 80f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
    }
    canvas.drawText(standing.positionWithSuffix, 20f, photoHeight - 40f, rankPaint)

    // "Global Rank" text
    val globalRankPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#E6FFFFFF")
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = Paint.Align.LEFT
    }
    canvas.drawText("Global Rank", 22f, photoHeight - 16f, globalRankPaint)

    // ---- Bottom section: Dark blue info panel ----
    val infoPaint = Paint().apply {
        color = AndroidColor.parseColor("#232B5B")
    }
    canvas.drawRect(0f, photoHeight.toFloat(), width.toFloat(), totalHeight.toFloat(), infoPaint)

    val infoTop = photoHeight.toFloat()

    // Pilot name with flag
    val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 22f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
    }
    canvas.drawText(standing.displayName, 20f, infoTop + 30f, namePaint)

    // Subtitle
    val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#6D6D77")
        textSize = 16f
        textAlign = Paint.Align.LEFT
    }
    canvas.drawText("Fastest 3 Consecutive Laps", 20f, infoTop + 52f, subtitlePaint)

    // Score labels in yellow
    val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#F9D749")
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
    }

    var scoreY = infoTop + 85f
    if (standing.score1Label.isNotEmpty()) {
        canvas.drawText(standing.score1Label, 20f, scoreY, scorePaint)
        scoreY += 26f
    }
    if (standing.score2Label.isNotEmpty()) {
        canvas.drawText(standing.score2Label, 20f, scoreY, scorePaint)
    }

    // GQ MultiGP logo (bottom-right)
    val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.standing_badge_logo)
    if (logoBitmap != null) {
        val logoH = 80
        val logoW = (logoBitmap.width.toFloat() / logoBitmap.height * logoH).toInt()
        val logoX = width - logoW - 20
        val logoY = (infoTop + 65f).toInt()
        val logoDest = Rect(logoX, logoY, logoX + logoW, logoY + logoH)
        canvas.drawBitmap(logoBitmap, null, logoDest, null)
    }

    return bitmap
}
