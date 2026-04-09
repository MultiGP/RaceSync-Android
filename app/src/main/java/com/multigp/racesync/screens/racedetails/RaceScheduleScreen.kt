package com.multigp.racesync.screens.racedetails

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView

/**
 * Displays the race schedule/itinerary in a WebView.
 * Matches iOS RaceScheduleViewController which loads the itinerary HTML content.
 * Falls back to a placeholder message when no schedule content is available.
 */
@Composable
fun RaceScheduleScreen(
    race: Race,
    raceView: RaceView,
    modifier: Modifier = Modifier
) {
    val itineraryHtml = raceView.itineraryContent

    if (itineraryHtml.isBlank()) {
        // No schedule available — placeholder
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No schedule available for this event.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(32.dp)
            )
        }
    } else {
        // Render itinerary HTML in a WebView (matches iOS's WKWebView approach)
        val styledHtml = """
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: -apple-system, Roboto, sans-serif;
                        font-size: 16px;
                        color: #333333;
                        padding: 16px;
                        margin: 0;
                        line-height: 1.5;
                    }
                    hr { border: 0; border-top: 1px solid #CACACF; margin: 12px 0; }
                    p { margin: 8px 0; }
                </style>
            </head>
            <body>$itineraryHtml</body>
            </html>
        """.trimIndent()

        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = false
                    loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
            }
        )
    }
}
