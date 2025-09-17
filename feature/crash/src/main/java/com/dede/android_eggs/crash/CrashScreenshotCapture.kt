package com.dede.android_eggs.crash

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.PixelCopy
import android.view.View
import androidx.annotation.WorkerThread
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.dede.android_eggs.util.noOpDelegate
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
import kotlin.math.roundToInt

object CrashScreenshotCapture : Application.ActivityLifecycleCallbacks by noOpDelegate(),
    OnRootViewsChangedListener {

    private const val TAG = "ScreenshotCapture"

    private var topActivityRef: WeakReference<Activity>? = null
    private var rootViewRef: WeakReference<View>? = null
    private var touchPoint = PointF(0f, 0f)

    fun getActivity(): Activity? = topActivityRef?.get()

    fun getRootView(): View? = rootViewRef?.get()

    fun getWindowTouchPoint(): PointF = PointF(touchPoint.x, touchPoint.y)

    override fun onActivityResumed(activity: Activity) {
        topActivityRef = WeakReference(activity)
    }

    override fun onRootViewsChanged(view: View, added: Boolean) {
        if (added) {
            if (view.windowAttachCount == 0) {
                view.phoneWindow?.let { window ->
                    window.touchEventInterceptors += OnTouchEventListener { event ->
                        touchPoint.set(event.x, event.y)
                    }
                }
            }
            rootViewRef = WeakReference(view)
        } else {
            val lastRootView = Curtains.rootViews.lastOrNull()
            rootViewRef = if (lastRootView != null) WeakReference(lastRootView) else null
        }
    }

    fun initialize(context: Context) {
        val application = context.applicationContext as Application
        Curtains.onRootViewsChangedListeners += this
        application.registerActivityLifecycleCallbacks(this)
    }

    suspend fun tryCaptureScreenshot(context: Context): File? {
        val view = getRootView()
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
        PixelCopy.request(phoneWindow, rect, bitmap, { result: Int ->
            Log.i(TAG, "captureScreenshot result: $result")
        }, uiHandler)

        val point = getWindowTouchPoint()
        if (rect.contains(point.x.roundToInt(), point.y.roundToInt())) {
            bitmap.applyCanvas {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.setColor(Color.RED)
                drawCircle(point.x, point.y, 8.dpf, paint)
            }
        }

        return withContext(Dispatchers.IO) { saveScreenshot(context, bitmap) }
    }

    @WorkerThread
    private fun saveScreenshot(context: Context, bitmap: Bitmap): File {
        val dir = File(context.cacheDir, "crash")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, "screenshot_${System.currentTimeMillis()}.webp")
        if (file.exists()) {
            file.delete()
        }
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSLESS
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
        runCatching {
            file.outputStream().use {
                bitmap.compress(format, 80, it)
            }
        }
        return file
    }
}
