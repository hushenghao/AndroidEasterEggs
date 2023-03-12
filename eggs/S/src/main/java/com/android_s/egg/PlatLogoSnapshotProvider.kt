package com.android_s.egg

import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import kotlin.math.min

class PlatLogoSnapshotProvider : com.dede.basic.PlatLogoSnapshotProvider() {

    override fun create(context: Context): View {
        val layout = FrameLayout(context)

        val dm: DisplayMetrics = context.resources.displayMetrics
        val minSide = min(dm.widthPixels, dm.heightPixels)
        val widgetSize = (minSide * 0.75).toInt()

        val logo = ImageView(context)
        logo.setImageResource(R.drawable.s_platlogo)
        val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(widgetSize, widgetSize)
        lp.gravity = Gravity.CENTER
        layout.addView(logo, lp)

        return layout
    }
}