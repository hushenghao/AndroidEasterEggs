package com.dede.android_eggs.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ActivityEasterEggsBinding
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.main.StartupPage

/**
 * Easter Egg Collection
 */
class EasterEggsActivity : AppCompatActivity(R.layout.activity_easter_eggs) {

    private val binding: ActivityEasterEggsBinding by viewBinding(ActivityEasterEggsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
        EdgeUtils.applyEdge(window)
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        BackPressedHandler(this).register()

        StartupPage.show(this)

        SchemeHandler.handleIntent(this, intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        SchemeHandler.handleIntent(this, intent)
    }

}
