package com.dede.android_eggs.views.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.main.compose.AndroidSnapshotView
import com.dede.android_eggs.views.main.compose.EasterEggItem
import com.dede.android_eggs.views.main.compose.LocalEasterEggLogoSensor
import com.dede.android_eggs.views.main.compose.LocalFragmentManager
import com.dede.android_eggs.views.main.compose.LocalHost
import com.dede.android_eggs.views.main.compose.MainTitleBar
import com.dede.android_eggs.views.main.compose.ProjectDescription
import com.dede.android_eggs.views.main.compose.Wavy
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
        EdgeUtils.applyEdge(window)
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

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            LazyColumn(
                                contentPadding = contentPadding,
                                modifier = Modifier.sizeIn(maxWidth = 560.dp),
                            ) {
                                item {
                                    AndroidSnapshotView()
                                    Wavy(res = R.drawable.ic_wavy_line)
                                    for (easterEgg in easterEggs) {
                                        EasterEggItem(easterEgg)
                                    }
                                    Wavy(res = R.drawable.ic_wavy_line)
                                    ProjectDescription()
                                }
                            }
                        }
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
