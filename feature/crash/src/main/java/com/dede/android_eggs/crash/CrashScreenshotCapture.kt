package com.dede.android_eggs.crash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.dede.basic.dpf
import com.dede.basic.uiHandler
import curtains.Curtains
import curtains.OnRootViewsChangedListener
import curtains.OnTouchEventListener
import curtains.phoneWindow
import curtains.touchEventInterceptors
import curtains.windowAttachCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

object CrashScreenshotCapture : OnRootViewsChangedListener {

    private const val TAG = "ScreenshotCapture"

    private const val PIXEL_COPY_TIMEOUT_MS = 1_000L

    // PixelCopy's result callback must not depend on the main looper: a crash on the main
    // thread blocks it inside runBlocking, so a callback posted to uiHandler never runs.
    private val pixelCopyHandler by lazy { HandlerThread("crash-pixel-copy").apply { start() } }

    /**
     * Immutable snapshot of the last touch: the window it was received on plus the point in
     * that window's local coordinates. Swapped as a single reference so the crash thread
     * always reads a consistent window/point pair.
     */
    private class TouchMark(val window: Window, val x: Float, val y: Float)

    @Volatile
    private var rootViewRef: WeakReference<View>? = null

    @Volatile
    private var touchMark: TouchMark? = null

    override fun onRootViewsChanged(view: View, added: Boolean) {
        if (added) {
            if (view.windowAttachCount == 0) {
                view.phoneWindow?.let { window ->
                    // Root view notifications can arrive on any thread (e.g. a toast window
                    // added from its TN thread); registration must stay on the main thread
                    // because the interceptor list is iterated there while dispatching touches.
                    uiHandler.post {
                        window.touchEventInterceptors += OnTouchEventListener { event ->
                            // Report the most recently pressed pointer.
                            val pointerIndex = event.pointerCount - 1
                            touchMark = TouchMark(window, event.getX(pointerIndex), event.getY(pointerIndex))
                        }
                    }
                }
            }
            // Only capturable windows are tracked: toasts, popups and tooltips have no
            // PhoneWindow and must not shadow the capturable window underneath.
            if (view.phoneWindow != null) {
                rootViewRef = WeakReference(view)
            }
        } else {
            if (view.phoneWindow === touchMark?.window) {
                // The window goes away: drop the touch mark so a stale point from
                // this window is never drawn again, even if it is re-added later.
                touchMark = null
            }
            val lastRootView = Curtains.rootViews.lastOrNull { it.phoneWindow != null }
            rootViewRef = if (lastRootView != null) WeakReference(lastRootView) else null
        }
    }

    fun initialize() {
        Curtains.onRootViewsChangedListeners += this
    }

    suspend fun tryCaptureScreenshot(context: Context): File? {
        val view = rootViewRef?.get()
        if (view == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return null
        }
        val phoneWindow = view.phoneWindow ?: return null
        val rootView = phoneWindow.decorView
        if (rootView.width <= 0 || rootView.height <= 0 || !rootView.isShown) {
            return null
        }

        val rect = Rect(0, 0, rootView.width, rootView.height)
        val bitmap = createBitmap(rect.width(), rect.height())
        // Skip the screenshot entirely when the copy fails or times out: a half-filled
        // bitmap would be saved as a blank image, marker included.
        if (!runCatching { copyWindowToBitmap(phoneWindow, rect, bitmap) }.getOrDefault(false)) {
            return null
        }

        val mark = touchMark
        // The touch point is in the local coordinates of the window it was received on,
        // only draw the marker when it belongs to the captured window.
        val marker = mark?.takeIf {
            phoneWindow === it.window && rect.contains(it.x.roundToInt(), it.y.roundToInt())
        }?.let { PointF(it.x, it.y) }

        return withContext(Dispatchers.IO) {
            marker?.let { drawTouchMarker(bitmap, it) }
            saveScreenshot(context, bitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun copyWindowToBitmap(window: Window, rect: Rect, bitmap: Bitmap): Boolean {
        val success = AtomicBoolean(false)
        val latch = CountDownLatch(1)
        PixelCopy.request(window, rect, bitmap, { result: Int ->
            success.set(result == PixelCopy.SUCCESS)
            latch.countDown()
            Log.i(TAG, "copyWindowToBitmap result: $result")
        }, Handler(pixelCopyHandler.looper))
        return latch.await(PIXEL_COPY_TIMEOUT_MS, TimeUnit.MILLISECONDS) && success.get()
    }

    private fun drawTouchMarker(bitmap: Bitmap, markerPoint: PointF) {
        bitmap.applyCanvas {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.setColor(Color.RED)
            drawCircle(markerPoint.x, markerPoint.y, 8.dpf, paint)
        }
    }

    @WorkerThread
    private fun saveScreenshot(context: Context, bitmap: Bitmap): File? {
        val dir = File(context.cacheDir, "crash")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        // Only the newest screenshot is ever read (by the crash screen of this crash);
        // drop stale ones so repeated crashes don't accumulate files.
        dir.listFiles { _, name -> name.startsWith("screenshot_") }?.forEach { it.delete() }

        val file = File(dir, "screenshot_${System.currentTimeMillis()}.webp")
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSLESS
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
        val saved = runCatching {
            file.outputStream().use {
                bitmap.compress(format, 80, it)
            }
        }.getOrDefault(false)
        if (!saved) {
            file.delete()
            return null
        }
        return file
    }
}
