package com.multigp.racesync.screens.landing

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.multigp.racesync.composables.topbars.CommonTopBar

@Composable
fun NotificationWebViewScreen(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {},
    url: String,
    title: String
) {
    val injectedJs = """
        (function() {
            function applyCustomChanges() {
                var style = document.createElement('style');
                style.innerHTML = ".topright { display: none !important; } .topcenter { display: none !important; } .verticalviewbutton { display: none !important; }";
                document.head.appendChild(style);
                
                var headers = document.querySelectorAll("h2");
                headers.forEach(function(header) {
                    if (header.textContent.trim().startsWith("Event:")) {
                        header.innerHTML = "&nbsp;";
                    }
                });
                
                history.scrollRestoration = 'manual';
            }

            if (document.readyState === 'complete') {
                applyCustomChanges();
            } else {
                window.addEventListener('load', applyCustomChanges);
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
                .padding(padding)
        ) {
            AndroidView(factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true      // ✅ Scale content to fit screen width
                    settings.useWideViewPort = true           // ✅ Use meta viewport scaling
                    settings.builtInZoomControls = false      // Optional: enable if you want zoom
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)

                            // Optional: inject viewport meta if the page lacks it
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

                            view?.evaluateJavascript(injectViewport, null)
                            view?.evaluateJavascript(injectedJs, null)
                        }
                    }
                    loadUrl(url)
                }
            })
        }
    }
}
