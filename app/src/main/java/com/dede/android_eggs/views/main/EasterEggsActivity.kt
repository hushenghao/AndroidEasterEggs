@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)

package com.dede.android_eggs.views.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.main.compose.AndroidSnapshotView
import com.dede.android_eggs.views.main.compose.EasterEggItemContent
import com.dede.android_eggs.views.main.compose.EasterEggItemFloor
import com.dede.android_eggs.views.main.compose.EasterEggItemSwipe
import com.dede.android_eggs.views.main.compose.LocalFragmentManager
import com.dede.android_eggs.views.main.compose.LocalHost
import com.dede.android_eggs.views.main.compose.MainTitleBar
import com.dede.android_eggs.views.main.compose.ProjectDescription
import com.dede.android_eggs.views.main.compose.Wavy
import com.dede.android_eggs.views.theme.AppTheme
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
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

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
        EdgeUtils.applyEdge(window)
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalFragmentManager provides supportFragmentManager,
                LocalHost provides this
            ) {
                AppTheme {
                    Scaffold(
                        topBar = { MainTitleBar() }
                    ) { contentPadding ->
                        LazyColumn(contentPadding = contentPadding) {
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

        BackPressedHandler(this).register()

        StartupPage.show(this)

        schemeHandler.handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        schemeHandler.handleIntent(intent)
    }
}

@Composable
fun EasterEggItem(base: BaseEasterEgg) {
    val context = LocalContext.current

    var groupIndex by remember { mutableIntStateOf(0) }
    val egg = when (base) {
        is EasterEgg -> base
        is EasterEggGroup -> base.eggs[groupIndex]
        else -> throw UnsupportedOperationException("Unsupported type: ${base.javaClass}")
    }
    val supportShortcut = remember(egg) { EggActionHelp.isSupportShortcut(egg) }
    var swipeProgress by remember { mutableFloatStateOf(0f) }

    EasterEggItemSwipe(
        floor = {
            EasterEggItemFloor(egg, supportShortcut, swipeProgress)
        },
        content = {
            EasterEggItemContent(egg, base) {
                groupIndex = it
            }
        },
        supportShortcut = supportShortcut,
        onSwipe = {
            swipeProgress = it
        },
        addShortcut = {
            EggActionHelp.addShortcut(context, egg)
        },
    )
}
