@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main

import android.app.assist.AssistContent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.android_next.egg.AndroidNextTimelineDialog
import com.dede.android_eggs.R
import com.dede.android_eggs.inject.FlavorFeatures
import com.dede.android_eggs.ui.composes.ReverseModalNavigationDrawer
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.util.compose.end
import com.dede.android_eggs.views.main.compose.AnimatorDisabledAlertDialog
import com.dede.android_eggs.views.main.compose.BottomSearchBar
import com.dede.android_eggs.views.main.compose.EasterEggScreen
import com.dede.android_eggs.views.main.compose.Konfetti
import com.dede.android_eggs.views.main.compose.LocalEasterEggLogoSensor
import com.dede.android_eggs.views.main.compose.LocalKonfettiState
import com.dede.android_eggs.views.main.compose.MainTitleBar
import com.dede.android_eggs.views.main.compose.Welcome
import com.dede.android_eggs.views.main.compose.rememberBottomSearchBarState
import com.dede.android_eggs.views.main.compose.rememberKonfettiState
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp
import com.dede.android_eggs.views.main.util.IntentHandler
import com.dede.android_eggs.views.settings.SettingsScreen
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.IconVisualEffectsPrefUtil
import com.dede.android_eggs.views.theme.AppTheme
import com.dede.basic.Utils
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@AndroidEntryPoint
class EasterEggsActivity : AppCompatActivity() {

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>

    @Inject
    lateinit var pureEasterEggs: List<@JvmSuppressWildcards EasterEgg>

    @Inject
    @ActivityScoped
    lateinit var intentHandler: IntentHandler

    private var orientationAngleSensor: OrientationAngleSensor? = null

    @Inject
    lateinit var sensor: EasterEggLogoSensorMatrixConvert

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.enableEdgeToEdge(this)
        super.onCreate(savedInstanceState)

        setContent {
            val konfettiState = rememberKonfettiState()
            CompositionLocalProvider(
                LocalEasterEggLogoSensor provides sensor,
                LocalKonfettiState provides konfettiState
            ) {
                AppTheme {
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    ReverseModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerState = drawerState,
                                drawerShape = shapes.extraLarge.end(0.dp),
                                windowInsets = WindowInsets(0, 0, 0, 0)
                            ) {
                                val maxWidth =
                                    LocalConfiguration.current.smallestScreenWidthDp * 0.8f
                                Box(modifier = Modifier.width(maxWidth.dp)) {
                                    SettingsScreen(drawerState)
                                }
                            }
                        },
                        drawerState = drawerState
                    ) {
                        val searchBarState = rememberBottomSearchBarState()
                        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                        Scaffold(
                            topBar = {
                                MainTitleBar(
                                    scrollBehavior = scrollBehavior,
                                    searchBarState = searchBarState,
                                    drawerState = drawerState,
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
                    }

                    val context = LocalContext.current
                    val animatorDisabledAlertState = remember { mutableStateOf(false) }

                    Welcome(onNext = {
                        if (!Utils.areAnimatorEnabled(context)) {
                            animatorDisabledAlertState.value = true
                        }
                    })

                    AnimatorDisabledAlertDialog(animatorDisabledAlertState)

                    Konfetti(konfettiState)

                    AndroidNextTimelineDialog()
                }
            }
        }

        handleOrientationAngleSensor(IconVisualEffectsPrefUtil.isEnable(this))
        LocalEvent.receiver(this).register(IconVisualEffectsPrefUtil.ACTION_CHANGED) {
            val enable = it.getBooleanExtra(SettingPrefUtil.EXTRA_VALUE, false)
            handleOrientationAngleSensor(enable)
        }

        intentHandler.handleIntent(intent)
        EasterEggShortcutsHelp.updateShortcuts(this, pureEasterEggs)

        // call flavor features
        FlavorFeatures.get().call(this)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentHandler.handleIntent(intent)
    }

    override fun onProvideAssistContent(outContent: AssistContent?) {
        super.onProvideAssistContent(outContent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && outContent != null) {
            outContent.webUri = getString(R.string.url_github).toUri()
        }
    }
}
