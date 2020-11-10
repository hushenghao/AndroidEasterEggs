package com.dede.android_eggs

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.activity_easter_eggs.*
import kotlin.math.hypot

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
        ll_content.visibility = View.INVISIBLE
        ll_content.postDelayed(this, 300)
    }

    override fun run() {
        val cx = iv_splash.x + iv_splash.width / 2f
        val cy = iv_splash.y + iv_splash.height / 2f
        val startRadius = hypot(iv_splash.width.toFloat(), iv_splash.height.toFloat())
        val endRadius = hypot(ll_content.width.toFloat(), ll_content.height.toFloat())
        val circularAnim =
            ViewAnimationUtils.createCircularReveal(
                ll_content,
                cx.toInt(), cy.toInt(), startRadius, endRadius
            ).apply {
                duration = 1000
                addListener(onStart = {
                    ll_content.visibility = View.VISIBLE
                })
            }
        val alphaAnim = ObjectAnimator.ofFloat(iv_splash, "alpha", 1f, 0f)
            .apply {
                duration = 800
                addListener(onEnd = {
                    iv_splash.visibility = View.GONE
                })
            }
        AnimatorSet().apply {
            playTogether(circularAnim, alphaAnim)
        }.start()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

    }

}
