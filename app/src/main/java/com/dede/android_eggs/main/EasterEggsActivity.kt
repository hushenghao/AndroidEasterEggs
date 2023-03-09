package com.dede.android_eggs.main

import android.animation.ObjectAnimator
import android.content.res.Configuration
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
import com.google.android.material.R as M3R

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(), MenuProvider {

    private val navigationViewController = NavigationViewController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowEdgeUtilsAccessor.applyEdgeToEdge(window, true)

        navigationViewController.setContentView()

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
        menu.findItem(R.id.menu_settings).icon =
            FontIconsDrawable(this, "\ue8b8", M3R.attr.actionMenuTextColor, 24f)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_settings -> {
                val icon = menuItem.icon as FontIconsDrawable
                SettingsFragment().apply {
                    onSlide = {
                        icon.setRotate(360f * it)
                    }
                    show(supportFragmentManager, "Settings")
                }
                ObjectAnimator.ofFloat(icon, "rotate", 0f, 360f)
                    .setDuration(500)
                    .start()
            }
            else -> return false
        }
        return true
    }

}
