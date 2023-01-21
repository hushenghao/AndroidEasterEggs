package com.dede.android_eggs

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.google.android.material.internal.EdgeToEdgeUtils

/**
 * Chrome Dino Egg.
 *
 * @author shhu
 * @since 2023/1/21
 */
class DinoEggActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("RestrictedApi", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)

        webView = WebView(this)
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.displayZoomControls = false
        settings.loadWithOverviewMode = false
        settings.useWideViewPort = true
        settings.setSupportZoom(false)
        settings.domStorageEnabled = true// require dom storage
        webView.loadUrl("file:///android_asset/chrome-dino-enhanced/index.html")

        setContentView(webView)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        webView.requestFocus()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView.stopLoading()
        webView.destroy()
        super.onDestroy()
    }
}