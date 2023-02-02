package com.dede.android_eggs

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.dede.android_eggs.databinding.ActivityEasterEggsLandBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.internal.EdgeToEdgeUtils

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity() {

    private val navigationViewController = NavigationViewController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)
        @Suppress("RestrictedApi")
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)

        if (isWideSize()) {
            navigationViewController.bind(ActivityEasterEggsLandBinding.inflate(layoutInflater))
        } else {
            navigationViewController.bind(ActivityEasterEggsBinding.inflate(layoutInflater))
        }

        if (savedInstanceState == null) {
            EasterEggsSplash(this, window.decorView).welcome()
        }
    }

    fun isWideSize(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                resources.configuration.smallestScreenWidthDp >= 600
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        navigationViewController.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return navigationViewController.onOptionsItemSelected(item) ||
                super.onOptionsItemSelected(item)
    }

}
