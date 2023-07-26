package com.dede.android_eggs.main

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.basic.dp
import kotlin.random.Random

/**
 * Split Placeholder
 *
 * @author shhu
 * @since 2023/5/22
 */
class PlaceholderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        EdgeUtils.tryApplyOLEDTheme(this)
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
    }

    private fun randomRes(): Int {
        val array = intArrayOf(
            com.android_u.egg.R.drawable.u_android14_patch_adaptive,
            R.drawable.ic_android_tiramisu,
            R.drawable.ic_android_s,
            com.android_r.egg.R.drawable.r_icon,
            com.android_q.egg.R.drawable.q_icon,
            com.android_p.egg.R.drawable.p_icon,
            R.drawable.ic_android_oreo,
            R.drawable.ic_android_nougat,
            R.drawable.ic_android_marshmallow,
            R.drawable.ic_android_lollipop,
        )
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