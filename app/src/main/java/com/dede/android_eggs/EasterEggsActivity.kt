package com.dede.android_eggs

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.internal.EdgeToEdgeUtils
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.math.hypot

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(), Runnable {

    private lateinit var binding: ActivityEasterEggsBinding
    private lateinit var ivLogo: ImageView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        binding = ActivityEasterEggsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.appBar.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(this)

        actionBarDrawerToggle = ActionBarDrawerToggle(this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.label_drawer_open,
            R.string.label_drawer_close).apply { syncState() }

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_github -> {
                    ChromeTabsBrowser.launchUrl(this, Uri.parse(getString(R.string.url_github)))
                }
                R.id.menu_source -> {
                    ChromeTabsBrowser.launchUrl(this, Uri.parse(getString(R.string.url_source)))
                }
                R.id.menu_privacy_agreement -> {
                    ChromeTabsBrowser.launchUrl(this,
                        Uri.parse(getString(R.string.url_privacy_agreement)))
                }
                R.id.menu_beta -> {
                    ChromeTabsBrowser.launchUrl(this, Uri.parse(getString(R.string.url_beta)))
                }
            }
            return@setNavigationItemSelectedListener true
        }
        val headerView = binding.navigationView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.tv_version).text =
            getString(R.string.summary_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE)
        ViewCompat.setOnApplyWindowInsetsListener(headerView,
            OnApplyWindowInsetsListener { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.updatePadding(top = systemBars.top)
                return@OnApplyWindowInsetsListener insets
            })

        ivLogo = AppCompatImageView(this).apply {
            setImageResource(R.drawable.t_platlogo)
        }
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER }
        addContentView(ivLogo, layoutParams)

        postAnim()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun postAnim() {
        binding.root.visibility = View.INVISIBLE
        binding.root.post(this)
    }

    override fun run() {
        val logo = ivLogo
        val content = binding.root
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
        binding.root.removeCallbacks(this)
        super.onDestroy()
    }

}
