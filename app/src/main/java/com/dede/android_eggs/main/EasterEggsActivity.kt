package com.dede.android_eggs.main

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.dede.android_eggs.settings.EdgePref
import com.dede.android_eggs.settings.SettingsPageController

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity() {

    private val settingsPageController = SettingsPageController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EdgePref.applyEdge(this, window)

        val binding = ActivityEasterEggsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        settingsPageController.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            EasterEggsSplash(this).welcome()
        }
    }

}
