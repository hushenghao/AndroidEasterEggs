package com.dede.android_eggs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.dede.basic.dp
import com.google.android.material.color.DynamicColors
import com.google.android.material.internal.EdgeToEdgeUtils
import fi.iki.elonen.NanoHTTPD
import java.io.IOException

/**
 * Dino Egg.
 *
 * @author shhu
 * @since 2023/1/21
 */
class DinoEggActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var server: DinoServer

    @SuppressLint("RestrictedApi", "SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)

        webView = WebView(this)
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.displayZoomControls = false
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportZoom(false)
        settings.domStorageEnabled = true// require dom storage
        //webView.loadUrl("file:///android_asset/chrome-dino-enhanced/index.html")

        setContentView(webView)

        server = DinoServer(applicationContext).apply { launch() }
        webView.setOnTouchListener(WebViewDinoController())
        webView.loadUrl("http://127.0.0.1:8888/dino3d/low.html")

        val back = AppCompatImageView(this).apply {
            val iconsDrawable = FontIconsDrawable(this.context, "\ue2ea", 40f)
            iconsDrawable.setColor(Color.WHITE)
            iconsDrawable.setPadding(8.dp)
            setImageDrawable(iconsDrawable)
            setOnClickListener {
                finish()
            }
        }
        addContentView(
            back,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )
        ViewCompat.setOnApplyWindowInsetsListener(back, OnApplyWindowInsetsListener { v, insets ->
            val inset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<MarginLayoutParams> {
                topMargin = inset.top + 10.dp
                marginStart = inset.left + 10.dp
            }
            return@OnApplyWindowInsetsListener insets
        })
    }

    private class WebViewDinoController : View.OnTouchListener {
        private var downTime: Long = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(webView: View, event: MotionEvent): Boolean {
            val keyCode = if (event.y > webView.height * 2 / 3f)
                KeyEvent.KEYCODE_DPAD_DOWN  // Arrow Down
            else
                KeyEvent.KEYCODE_DPAD_UP    // Arrow Up
            val keyAction = when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downTime = SystemClock.uptimeMillis()
                    KeyEvent.ACTION_DOWN
                }
                MotionEvent.ACTION_UP -> KeyEvent.ACTION_UP
                else -> return false
            }
            val keyEvent = createKeyEvent(keyAction, keyCode, downTime)
            // Simulate keyboard events
            webView.dispatchKeyEvent(keyEvent)
            return false
        }

        private fun createKeyEvent(keyAction: Int, keyCode: Int, downTime: Long): KeyEvent {
            return KeyEvent(downTime, SystemClock.uptimeMillis(), keyAction, keyCode, 0)
        }
    }

    private class DinoServer(val context: Context) : NanoHTTPD("127.0.0.1", 8888) {

        fun launch() {
            try {
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun serve(session: IHTTPSession): Response {
            Log.i("NanoHTTPD", "serve: " + session.uri)
            val stream = try {
                context.assets.open(session.uri.substring(1))
            } catch (e: IOException) {
                e.printStackTrace()
                return super.serve(session)
            }
            return newFixedLengthResponse(
                Response.Status.OK,
                getMimeTypeForFile(session.uri),
                stream,
                stream.available().toLong()
            )
        }
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
        server.stop()
        super.onDestroy()
    }
}