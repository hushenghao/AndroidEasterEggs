package com.dede.android_eggs.views.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import com.dede.android_eggs.R
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.main.compose.AndroidSnapshotView
import com.dede.android_eggs.views.main.compose.EasterEggItem
import com.dede.android_eggs.views.main.compose.LocalFragmentManager
import com.dede.android_eggs.views.main.compose.LocalHost
import com.dede.android_eggs.views.main.compose.MainTitleBar
import com.dede.android_eggs.views.main.compose.ProjectDescription
import com.dede.android_eggs.views.main.compose.Wavy
import com.dede.android_eggs.views.main.compose.Welcome
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
                        Welcome()

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

        schemeHandler.handleIntent(intent)
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        schemeHandler.handleIntent(intent)
    }
}
