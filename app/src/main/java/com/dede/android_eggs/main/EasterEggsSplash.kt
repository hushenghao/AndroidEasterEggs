package com.dede.android_eggs.main

import android.graphics.drawable.Animatable
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dede.android_eggs.R
import com.dede.basic.dp
import kotlin.math.hypot

class EasterEggsSplash(private val activity: AppCompatActivity) : Runnable,
    DefaultLifecycleObserver {

    private lateinit var ivLogo: ImageView
    private lateinit var root: View

    fun welcome() {
        root = activity.findViewById(android.R.id.content)
        ivLogo = AppCompatImageView(activity).apply {
            setImageResource(R.drawable.avd_anim)
        }
        (ivLogo.drawable as Animatable).start()
        val layoutParams = FrameLayout.LayoutParams(192.dp, 192.dp)
            .apply { gravity = Gravity.CENTER }
        activity.addContentView(ivLogo, layoutParams)

        root.visibility = View.INVISIBLE
        root.post(this)

        activity.lifecycle.addObserver(this)
    }

    override fun run() {
        val logo = this.ivLogo
        val content = this.root
        val cx = logo.x + logo.width / 2f
        val cy = logo.y + logo.height / 2f
        val startRadius = hypot(logo.width.toFloat(), logo.height.toFloat())
        val endRadius = hypot(content.width.toFloat(), content.height.toFloat())
        val circularAnim = ViewAnimationUtils
            .createCircularReveal(content, cx.toInt(), cy.toInt(), startRadius, endRadius)
            .setDuration(800)
        logo.animate()
            .alpha(0f)
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(600)
            .withEndAction {
                logo.visibility = View.GONE
            }
            .withStartAction {
                content.visibility = View.VISIBLE
                circularAnim.start()
            }
            .start()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        root.removeCallbacks(this)
    }
}