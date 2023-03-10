package com.dede.android_eggs.main

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dede.android_eggs.settings.EdgePref
import com.dede.android_eggs.settings.SettingsPageController

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity() {

    private val navigationViewController = NavigationViewController(this)
    private val settingsPageController = SettingsPageController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EdgePref.applyEdge(this, window)
        navigationViewController.setContentView()
        settingsPageController.onCreate(savedInstanceState)
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
