package com.multigp.racesync.screens.landing

import android.content.Intent
import android.net.Uri
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTopBar(
    title: String,
    urlToOpen: String,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen))
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Browser")
            }
        }
    )
}


