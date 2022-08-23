package com.dede.android_eggs

import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.google.android.material.internal.EdgeToEdgeUtils
import kotlin.math.hypot

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(), Runnable {

    private lateinit var binding: ActivityEasterEggsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatusBar()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        binding = ActivityEasterEggsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        postAnim()
    }

    @Suppress("DEPRECATION")
    private fun initStatusBar() {
        val option =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        val decorView = window.decorView
        val visibility: Int = decorView.systemUiVisibility
        decorView.systemUiVisibility = visibility or option
    }

    private fun postAnim() {
        binding.content.visibility = View.INVISIBLE
        binding.content.postDelayed(this, 200)
    }

    override fun run() {
        val logo = binding.logo
        val content = binding.content
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

    override fun onDestroy() {
        binding.content.removeCallbacks(this)
        super.onDestroy()
    }

}
