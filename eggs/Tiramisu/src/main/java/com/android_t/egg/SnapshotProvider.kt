package com.android_t.egg

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.dede.android_eggs.system_colors.SystemColorDrawables
import com.dede.basic.provider.SnapshotProvider

class SnapshotProvider : SnapshotProvider() {

    override fun create(context: Context): View {
        val layout = FrameLayout(context)

        val logo = ImageView(context)
        logo.setImageDrawable(
            SystemColorDrawables.create(
                context,
                "system_accent1_400",
                R.drawable.t_platlogo_bg,
                R.drawable.t_platlogo_nobg,
                R.drawable.t_platlogo,
            )
        )
        val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        lp.gravity = Gravity.CENTER
        layout.addView(logo, lp)

        return layout
    }
}