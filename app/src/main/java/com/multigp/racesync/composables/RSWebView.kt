package com.multigp.racesync.composables

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun RSWebView(
    modifier: Modifier = Modifier,
    url: String
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                }
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                settings.loadWithOverviewMode = true
                settings.setSupportZoom(true)
                loadUrl(url)
            }
        },
        update = { view ->
            view.loadUrl(url)
        },
        modifier = modifier.fillMaxSize()
    )
}