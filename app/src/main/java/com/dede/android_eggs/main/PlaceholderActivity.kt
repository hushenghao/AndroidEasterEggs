package com.dede.android_eggs.main

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.dede.android_eggs.R
import com.dede.android_eggs.settings.EdgePref
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import kotlin.random.Random

/**
 * Split Placeholder
 *
 * @author shhu
 * @since 2023/5/22
 */
class PlaceholderActivity : AppCompatActivity(R.layout.activity_placeholder) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EdgePref.applyEdge(this, window)
        LocalEvent.get(this as LifecycleOwner).register(EdgePref.ACTION_CHANGED) {
            recreate()
        }
        val drawable = AlterableAdaptiveIconDrawable(this, randomRes(), randomPath())
        findViewById<ImageView>(R.id.iv_icon).apply {
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
    }

    private fun randomRes(): Int {
        val array = intArrayOf(
            R.drawable.ic_android_udc,
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
        // 排除前两个
        val index = Random.nextInt(array.size - 2) + 2
        return array[index]
    }
}