package com.dede.android_eggs.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.basic.GlobalContext
import com.skydoves.compose.stability.runtime.ComposeStabilityAnalyzer

class ApplicationInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val application = context.applicationContext as Application
        // setup states value
        SettingPrefUtil.setup(application)
        // apply compat style
        ThemePrefUtil.apply(application)

        // Enable recomposition tracking ONLY in debug builds
        ComposeStabilityAnalyzer.setEnabled(BuildConfig.DEBUG)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        GlobalContext.Initializer::class.java
    )
}