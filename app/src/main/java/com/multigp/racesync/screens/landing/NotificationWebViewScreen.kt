package com.multigp.racesync.screens.landing

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun NotificationWebViewScreen(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {},
    url: String,
    title: String
) {
    var isLoading by remember { mutableStateOf(true) }

    val injectedJs = """
                    (function () {
                        function applyCustomChanges() {
                            var style = document.createElement('style');
                            style.innerHTML = ".topright, .topcenter, .verticalviewbutton, .elementor.elementor-location-header, .elementor-element-964145d, #topPilotTable_length { display: none !important; } #topPilotTable_filter { display:inline !important; text-align: left !important; width: 100% !important; padding: 0 16px;} #topPilotTable_filter input { width: 100% !important; }";
                            document.head.appendChild(style);

                            // Remove Event headers
                            var headers = document.querySelectorAll("h2");
                            headers.forEach(function(header) {
                                if (header.textContent.trim().startsWith("Event:")) {
                                    header.innerHTML = "&nbsp;";
                                }
                            });

                            // Keep trying to update the search box until it appears
                            const intervalId = setInterval(() => {
                                const label = document.querySelector('#topPilotTable_filter label');
                                if (label) {
                                    const input = label.querySelector('input');
                                    if (input) {
                                        input.placeholder = 'Search pilot name...';
                                        label.innerHTML = ''; // remove label text
                                        label.appendChild(input); // add only the input back
                                        clearInterval(intervalId); // stop once done
                                    }
                                }
                            }, 300); // try every 300ms
                        }

                        if (document.readyState === 'complete') {
                            applyCustomChanges();
                        } else {
                            window.addEventListener('load', applyCustomChanges);
                        }
                    })();




    """.trimIndent()

    val injectViewport = """
        (function() {
            if (!document.querySelector('meta[name="viewport"]')) {
                var meta = document.createElement('meta');
                meta.name = "viewport";
                meta.content = "width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no";
                document.head.appendChild(meta);
            }
        })();
    """.trimIndent()

    Scaffold(
        topBar = {
            NotificationTopBar(
                title = title,
                urlToOpen = url,
                onBackPressed = onMenuClicked
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()

        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(text = "2025 MULTIGP GLOBAL QUALIFIER - SPONSORED BY FINZ",modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                )
                AndroidView(factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.builtInZoomControls = false

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                view?.evaluateJavascript(injectViewport, null)
                                view?.evaluateJavascript(injectedJs, null)
                            }
                        }

                        loadUrl(url)
                    }
                })

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }

                }
            }
        }
    }
}
