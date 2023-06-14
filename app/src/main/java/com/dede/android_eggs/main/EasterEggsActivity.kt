package com.dede.android_eggs.main

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.dede.android_eggs.settings.EdgePref
import com.dede.android_eggs.settings.SettingsPageController

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(R.layout.activity_easter_eggs) {

    private val settingsPageController = SettingsPageController(this)
    private val binding: ActivityEasterEggsBinding by viewBinding(ActivityEasterEggsBinding::bind)

    private val appBarExpandedState = AppBarExpandedState(this, R.id.app_bar)

    override fun onCreate(savedInstanceState: Bundle?) {
        EdgePref.applyEdge(this, window)
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        settingsPageController.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            EasterEggsSplash(this).welcome()
        }
        appBarExpandedState.restore(savedInstanceState)
        BackPressedHandler(this).register()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        appBarExpandedState.saveState(outState)
        super.onSaveInstanceState(outState)
    }

}
