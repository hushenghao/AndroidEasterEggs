package com.android.launcher2

import android.service.dreams.DreamService
import android.view.ViewGroup
import kotlin.math.max

class RocketLauncherDream : DreamService() {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setInteractive(false)
        isFullscreen = true

        val metrics = resources.displayMetrics
        val longSide = max(metrics.widthPixels, metrics.heightPixels)

        val b = RocketLauncher.Board(this, null)
        setContentView(b, ViewGroup.LayoutParams(longSide, longSide))
        b.x = ((metrics.widthPixels - longSide) / 2).toFloat()
        b.y = ((metrics.heightPixels - longSide) / 2).toFloat()
    }

}
