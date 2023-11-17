package com.dede.android_eggs.views.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.main.compose.EasterEggScreen
import com.dede.android_eggs.views.main.compose.LocalEasterEggLogoSensor
import com.dede.android_eggs.views.main.compose.LocalFragmentManager
import com.dede.android_eggs.views.main.compose.LocalHost
import com.dede.android_eggs.views.main.compose.MainTitleBar
import com.dede.android_eggs.views.main.compose.Welcome
import com.dede.android_eggs.views.settings.prefs.IconVisualEffectsPref
import com.dede.android_eggs.views.theme.AppTheme
import com.dede.basic.provider.BaseEasterEgg
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@AndroidEntryPoint
class EasterEggsActivity : AppCompatActivity() {

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>

    @Inject
    @ActivityScoped
    lateinit var schemeHandler: SchemeHandler

    private var orientationAngleSensor: OrientationAngleSensor? = null

    @Inject
    lateinit var sensor: EasterEggLogoSensorMatrixConvert

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalFragmentManager provides supportFragmentManager,
                LocalHost provides this,
                LocalEasterEggLogoSensor provides sensor,
            ) {
                AppTheme {
                    Scaffold(
                        topBar = {
                            MainTitleBar()
                        }
                    ) { contentPadding ->
                        Welcome()

                        EasterEggScreen(easterEggs, contentPadding)
                    }
                }
            }
        }

        BackPressedHandler(this).register()

        handleOrientationAngleSensor(IconVisualEffectsPref.isEnable(this))
        LocalEvent.receiver(this).register(IconVisualEffectsPref.ACTION_CHANGED) {
            val enable = it.getBooleanExtra(IconVisualEffectsPref.EXTRA_VALUE, false)
            handleOrientationAngleSensor(enable)
        }

        schemeHandler.handleIntent(intent)
    }


    private fun handleOrientationAngleSensor(enable: Boolean) {
        val orientationAngleSensor = this.orientationAngleSensor
        if (enable && orientationAngleSensor == null) {
            this.orientationAngleSensor = OrientationAngleSensor(
                this, this, sensor
            )
        } else if (!enable && orientationAngleSensor != null) {
            orientationAngleSensor.destroy()
            this.orientationAngleSensor = null
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        schemeHandler.handleIntent(intent)
    }
}
