package com.dede.android_eggs.main

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

        navigationViewController.setContentView()

        if (savedInstanceState == null) {
            EasterEggsSplash(this).welcome()
        }
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
