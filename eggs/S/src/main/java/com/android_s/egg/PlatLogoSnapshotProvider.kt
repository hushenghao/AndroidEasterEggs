package com.android_s.egg

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.dede.basic.dp
import com.dede.basic.dpf

class PlatLogoSnapshotProvider : com.dede.basic.PlatLogoSnapshotProvider() {

    override fun create(context: Context): View {
        val layout = FrameLayout(context)

        val minSide = 360.dp
        val widgetSize = (minSide * 0.75f).toInt()

        val logo = ImageView(context)
        logo.setImageResource(R.drawable.s_platlogo)
        val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(widgetSize, widgetSize)
        lp.gravity = Gravity.CENTER
        layout.addView(logo, lp)

        val bg = PlatLogoActivity.BubblesDrawable(context)
        bg.level = 10000
        bg.avoid = widgetSize / 2f
        bg.padding = 0.5.dpf
        bg.minR = 1.dpf
        layout.background = bg

        return layout
    }
}