package com.dede.android_eggs.main

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.dede.android_eggs.R
import com.dede.android_eggs.settings.SettingsFragment
import com.dede.android_eggs.ui.FontIconsDrawable
import com.dede.android_eggs.util.WindowEdgeUtilsAccessor
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.android.material.R as M3R

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(), MenuProvider {

    private val navigationViewController = NavigationViewController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)
        WindowEdgeUtilsAccessor.applyEdgeToEdge(window, true)

        navigationViewController.setContentView()

        if (savedInstanceState == null) {
            EasterEggsSplash(this).welcome()
        }

        addMenuProvider(this, this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        navigationViewController.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return navigationViewController.onOptionsItemSelected(item) ||
                super.onOptionsItemSelected(item)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_settings, menu)
        val color = MaterialColors.getColor(this, M3R.attr.actionMenuTextColor, Color.GRAY)
        menu.findItem(R.id.menu_settings).icon = FontIconsDrawable(this, "\ue8b8", 24f).apply {
            setColor(color)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        SettingsFragment()
            .show(supportFragmentManager, "Settings")
        return true
    }

}
