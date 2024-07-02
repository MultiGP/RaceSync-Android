package com.multigp.racesync.screens.racedetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.buttons.ButtonWithIconAndText
import com.multigp.racesync.domain.model.Race

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapOptionsBottomSheet(
    race: Race,
    modifier: Modifier = Modifier,
    onSheetDissmissed: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onSheetDissmissed,
        sheetState = sheetState
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            ButtonWithIconAndText(
                label = R.string.map_option_google,
                icon = R.drawable.ic_google_map,
                onClick = {
                    launchMap(race, context)
                    onSheetDissmissed
                }
            )
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            ButtonWithIconAndText(
                label = R.string.map_option_waze,
                icon = R.drawable.ic_waze_map,
                onClick = {
                    launchWaze(race, context)
                    onSheetDissmissed()
                }
            )
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            ButtonWithIconAndText(
                label = R.string.map_option_copy_coordinates,
                icon = R.drawable.ic_coordinate,
                onClick = {
                    clipboardManager.setText(AnnotatedString(race.strLocation))
                    onSheetDissmissed()
                }
            )
            Spacer(modifier = modifier.height(16.dp))
        }
    }
}

fun launchMap(race: Race, context: Context) {
    val uri = Uri.parse("google.navigation:q=${race.getFormattedAddress()}&mode=d")
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}

fun launchWaze(race: Race, context: Context) {
    try {
        val uri = "waze://?ll=${race.strLocation}&navigate=yes"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"))
        context.startActivity(intent)
    }
}