package com.dede.android_eggs.settings

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.google.android.material.R as M3R


class SettingsPageController(private val activity: AppCompatActivity) : MenuProvider {

    companion object {
        private const val FRAGMENT_TAG = "Settings"
    }

    private lateinit var settingsIcon: FontIconsDrawable

    fun onCreate(savedInstanceState: Bundle?) {
        activity.addMenuProvider(this, activity)
        if (savedInstanceState != null) {
            tryBindSettingsPage()
        }
    }

    private fun tryBindSettingsPage() {
        val fm = activity.supportFragmentManager
        val settingsFragment = fm.findFragmentByTag(FRAGMENT_TAG) as? SettingsFragment
        if (settingsFragment != null) {
            settingsFragment.onSlide = this::onSlide
        }
    }

    private fun onSlide(offset: Float) {
        settingsIcon.setRotate(360f * offset)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_settings, menu)
        settingsIcon =
            FontIconsDrawable(activity, Icons.Rounded.settings, M3R.attr.colorControlNormal, 24f)
        menu.findItem(R.id.menu_settings).icon = settingsIcon
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_settings -> {
                SettingsFragment().apply {
                    onSlide = this@SettingsPageController::onSlide
                }.show(activity.supportFragmentManager, FRAGMENT_TAG)
                ObjectAnimator.ofFloat(settingsIcon, "rotate", 0f, 360f)
                    .setDuration(500)
                    .start()
            }
            else -> return false
        }
        return true
    }
}