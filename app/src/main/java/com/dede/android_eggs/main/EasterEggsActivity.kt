package com.dede.android_eggs.main

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dede.android_eggs.settings.SettingsPageController
import com.dede.android_eggs.util.WindowEdgeUtilsAccessor

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity() {

    private val navigationViewController = NavigationViewController(this)
    private val settingsPageController = SettingsPageController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowEdgeUtilsAccessor.applyEdgeToEdge(window, true)
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
