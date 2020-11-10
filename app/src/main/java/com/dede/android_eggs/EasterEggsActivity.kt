package com.dede.android_eggs

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.activity_easter_eggs.*
import kotlin.math.hypot

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(), Runnable {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatusBar()
        setContentView(R.layout.activity_easter_eggs)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_eggs, SettingsFragment())
                .commit()
        }
        postAnim()
    }

    private fun initStatusBar() {
        val option =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        val decorView = window.decorView
        val visibility: Int = decorView.systemUiVisibility
        decorView.systemUiVisibility = visibility or option
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun postAnim() {
        content.visibility = View.INVISIBLE
        content.postDelayed(this, 200)
    }

    override fun run() {
        val cx = logo.x + logo.width / 2f
        val cy = logo.y + logo.height / 2f
        val startRadius = hypot(logo.width.toFloat(), logo.height.toFloat())
        val endRadius = hypot(content.width.toFloat(), content.height.toFloat())
        val circularAnim = ViewAnimationUtils
            .createCircularReveal(content, cx.toInt(), cy.toInt(), startRadius, endRadius)
            .setDuration(800)
        circularAnim.addListener(
            onStart = {
                content.visibility = View.VISIBLE
            },
            onEnd = {
                logo.visibility = View.GONE
            }
        )
        val scaleYAnim = ObjectAnimator
            .ofFloat(logo, "scaleY", 1f, 1.3f)
            .setDuration(500)
        val scaleXAnim = ObjectAnimator
            .ofFloat(logo, "scaleX", 1f, 1.3f)
            .setDuration(500)
        val alphaAnim = ObjectAnimator
            .ofFloat(logo, "alpha", 1f, 0f)
            .setDuration(600)
        val set = AnimatorSet()
        set.interpolator = LinearInterpolator()
        set.play(circularAnim)
            .with(scaleXAnim)
            .with(scaleYAnim)
            .with(alphaAnim)
        set.start()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

    }

}
