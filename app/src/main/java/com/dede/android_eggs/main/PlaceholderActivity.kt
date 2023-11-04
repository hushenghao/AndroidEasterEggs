package com.dede.android_eggs.main

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.util.resolveColor
import com.dede.android_eggs.views.settings.prefs.NightModePref.Companion.ACTION_NIGHT_MODE_CHANGED
import com.dede.basic.dp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random
import com.google.android.material.R as M3R

/**
 * Split Placeholder
 *
 * @author shhu
 * @since 2023/5/22
 */
@AndroidEntryPoint
class PlaceholderActivity : AppCompatActivity() {

    @Inject
    lateinit var iconRes: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
        EdgeUtils.applyEdge(window)
        super.onCreate(savedInstanceState)

        val drawable = AlterableAdaptiveIconDrawable(this, randomRes(), randomPath())
        val imageView = ImageView(this).apply {
            setImageDrawable(drawable)
            scaleX = 0.5f
            scaleY = 0.5f
            alpha = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(300L)
                .start()
        }
        val params = FrameLayout.LayoutParams(56.dp, 56.dp).apply {
            gravity = Gravity.CENTER
        }
        setContentView(imageView, params)

        window.setBackgroundDrawable(ColorDrawable(resolveColor(M3R.attr.colorSurface)))

        LocalEvent.receiver(this)
            .register(ACTION_NIGHT_MODE_CHANGED) {
                recreate()
            }
    }

    private fun randomRes(): Int {
        val array = iconRes
        val index = Random.nextInt(array.size)
        return array[index]
    }

    private fun randomPath(): String {
        val array = resources.getStringArray(R.array.icon_shape_override_paths)
        // 排除第一个
        val index = Random.nextInt(array.size - 1) + 1
        return array[index]
    }
}