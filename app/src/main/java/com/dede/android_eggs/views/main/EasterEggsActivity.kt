@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main

import android.annotation.SuppressLint
import android.app.assist.AssistContent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.main.compose.BottomSearchBar
import com.dede.android_eggs.views.main.compose.EasterEggScreen
import com.dede.android_eggs.views.main.compose.Konfetti
import com.dede.android_eggs.views.main.compose.LocalEasterEggLogoSensor
import com.dede.android_eggs.views.main.compose.LocalFragmentManager
import com.dede.android_eggs.views.main.compose.LocalKonfettiState
import com.dede.android_eggs.views.main.compose.MainTitleBar
import com.dede.android_eggs.views.main.compose.Welcome
import com.dede.android_eggs.views.main.compose.rememberBottomSearchBarState
import com.dede.android_eggs.views.main.compose.rememberKonfettiState
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
        window.allowReturnTransitionOverlap = true
        super.onCreate(savedInstanceState)
        findViewById<ViewGroup>(android.R.id.content).isTransitionGroup = true

        setContent {
            val konfettiState = rememberKonfettiState()
            val searchBarState = rememberBottomSearchBarState()

            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            CompositionLocalProvider(
                LocalFragmentManager provides supportFragmentManager,
                LocalEasterEggLogoSensor provides sensor,
                LocalKonfettiState provides konfettiState
            ) {
                AppTheme {
                    Scaffold(
                        topBar = {
                            MainTitleBar(
                                scrollBehavior = scrollBehavior,
                                searchBarState = searchBarState,
                            )
                        },
                        modifier = Modifier
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        bottomBar = {
                            BottomSearchBar(searchBarState)
                        }
                    ) { contentPadding ->
                        EasterEggScreen(easterEggs, searchBarState.searchText, contentPadding)
                    }
                    Welcome()
                    Konfetti(konfettiState)
                }
            }
        }

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

    override fun onProvideAssistContent(outContent: AssistContent?) {
        super.onProvideAssistContent(outContent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && outContent != null) {
            outContent.webUri = getString(R.string.url_github).toUri()
        }
    }
}
