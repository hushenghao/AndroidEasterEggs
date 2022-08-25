package com.dede.android_eggs

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updatePadding
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.MaterialColors
import com.google.android.material.internal.EdgeToEdgeUtils
import kotlin.math.hypot
import com.google.android.material.R as MR

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(), Runnable {

    private lateinit var binding: ActivityEasterEggsBinding

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = DynamicColorsOptions.Builder()
            .setThemeOverlay(MR.style.ThemeOverlay_Material3_DynamicColors_DayNight)
            .build()
        DynamicColors.applyToActivityIfAvailable(this, options)
        val colorPrimary = MaterialColors.getColor(this, R.attr.colorPrimary, Color.WHITE)
        EdgeToEdgeUtils.applyEdgeToEdge(window, true, colorPrimary, null)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        binding = ActivityEasterEggsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val edge = insets.getInsets(Type.displayCutout() or Type.systemBars())
            view.updatePadding(top = edge.top)
            return@setOnApplyWindowInsetsListener insets
        }

        postAnim()
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
