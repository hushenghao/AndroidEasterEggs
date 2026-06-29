package com.android_b.egg

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.dede.basic.dp
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.requireDrawable

class BaseSnapshotProvider(
    @DrawableRes private val iconRes: Int,
) : SnapshotProvider() {

    override fun create(context: Context): View {
        val layout = FrameLayout(context)
        val image = ImageView(context).apply {
            setImageDrawable(context.requireDrawable(iconRes))
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        val params = FrameLayout.LayoutParams(150.dp, 150.dp).apply {
            gravity = Gravity.CENTER
        }
        layout.addView(image, params)
        return layout
    }
}
