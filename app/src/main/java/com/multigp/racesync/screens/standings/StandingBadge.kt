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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Standing
import com.multigp.racesync.domain.model.StandingSeason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

internal val BadgeDarkBlue = Color(0xFF232B5B)
internal val BadgeYellow = Color(0xFFF9D749)

@Composable
internal fun StandingBadgeDialog(
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
            StandingBadgePreview(
                standing = standing,
                season = season,
                profilePictureUrl = profilePictureUrl
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    text = stringResource(R.string.standings_share_to),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
internal fun StandingBadgePreview(
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
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profilePictureUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.standings_profile_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay
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

                // MultiGP logo (top-left)
                Image(
                    painter = painterResource(id = R.drawable.icn_mgp_small_white),
                    contentDescription = "MultiGP",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(36.dp)
                )

                // Year (top-right)
                Text(
                    text = season.value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )

                // Rank number overlay (bottom-left)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = standing.positionWithSuffix,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 64.sp
                    )
                    Text(
                        text = stringResource(R.string.standings_global_rank),
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
                Text(
                    text = standing.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.standings_fastest_laps),
                    fontSize = 12.sp,
                    color = Color(0xFF6D6D77)
                )

                Spacer(modifier = Modifier.height(12.dp))

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
// Share Badge as Image (coroutine-based)
// ============================================================

internal suspend fun shareBadgeImage(
    context: Context,
    standing: Standing,
    season: StandingSeason,
    profilePictureUrl: String?
) {
    val (bitmap, uri) = withContext(Dispatchers.IO) {
        val profileBitmap = downloadProfileBitmap(profilePictureUrl)
        val badge = renderBadgeBitmap(context, standing, season, profileBitmap)

        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "standing_badge.png")
        FileOutputStream(file).use { out ->
            badge.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        badge to fileUri
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(
            Intent.EXTRA_TEXT,
            "${standing.displayName} - ${standing.positionWithSuffix} in ${season.title}"
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    withContext(Dispatchers.Main) {
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.standings_share_standing)))
    }
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

    // Gradient overlay
    val overlayPaint = Paint().apply {
        shader = LinearGradient(
            0f, 0f,
            0f, photoHeight.toFloat(),
            intArrayOf(
                AndroidColor.parseColor("#00000000"),
                AndroidColor.parseColor("#33111133"),
                AndroidColor.parseColor("#99111133"),
                AndroidColor.parseColor("#CCCC0000")
            ),
            floatArrayOf(0f, 0.4f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), photoHeight.toFloat(), overlayPaint)

    // MultiGP icon (top-left)
    val mgpIcon = BitmapFactory.decodeResource(context.resources, R.drawable.icn_mgp_small_white)
    if (mgpIcon != null) {
        val iconSize = 50
        val iconDest = Rect(20, 20, 20 + iconSize, 20 + iconSize)
        canvas.drawBitmap(mgpIcon, null, iconDest, null)
    }

    // Year (top-right)
    val yearPaintTop = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 26f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.RIGHT
    }
    canvas.drawText(season.value, width - 20f, 42f, yearPaintTop)

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
    canvas.drawText(context.getString(R.string.standings_global_rank), 22f, photoHeight - 16f, globalRankPaint)

    // ---- Bottom section: Dark blue info panel ----
    val infoPaint = Paint().apply {
        color = AndroidColor.parseColor("#232B5B")
    }
    canvas.drawRect(0f, photoHeight.toFloat(), width.toFloat(), totalHeight.toFloat(), infoPaint)

    val infoTop = photoHeight.toFloat()

    val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        textSize = 22f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.LEFT
    }
    canvas.drawText(standing.displayName, 20f, infoTop + 30f, namePaint)

    val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#6D6D77")
        textSize = 16f
        textAlign = Paint.Align.LEFT
    }
    canvas.drawText(context.getString(R.string.standings_fastest_laps), 20f, infoTop + 52f, subtitlePaint)

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
        val logoY = (infoTop + 50f).toInt()
        val logoDest = Rect(logoX, logoY, logoX + logoW, logoY + logoH)
        canvas.drawBitmap(logoBitmap, null, logoDest, null)
    }

    return bitmap
}
