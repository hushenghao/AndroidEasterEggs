package com.android_v.egg

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.android_v.egg.PlatLogoActivity.Starfield
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.requireDrawable
import java.util.Random

class SnapshotProvider : SnapshotProvider() {

    override val includeBackground: Boolean
        get() = true

    override fun create(context: Context): View {
        val dp = context.resources.displayMetrics.density
        val random = Random()
        val starfield = Starfield(random, dp * 2.0f)
        starfield.setVelocity(
            (random.nextFloat() - 0.5f) * 200.0f,
            (random.nextFloat() - 0.5f) * 200.0f
        )
        val layout = FrameLayout(context)
        layout.background = starfield

        val logo = ImageView(context)
        logo.setImageDrawable(context.requireDrawable(R.drawable.v_platlogo))
        val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        lp.gravity = Gravity.CENTER
        layout.addView(logo, lp)

        return layout
    }
}