package com.dede.android_eggs.startup

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composer
import androidx.compose.runtime.tooling.ComposeStackTraceMode
import androidx.startup.Initializer
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.views.settings.compose.prefs.AppIconPrefUtil
import com.dede.android_eggs.views.theme.settings.ThemePrefUtil
import com.skydoves.compose.stability.runtime.ComposeStabilityAnalyzer

class ApplicationInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val application = context.applicationContext as Application
        // Preload DataStore snapshot before synchronous readers (theme, system colors, UI style).
        AppSettings.preload(application)
        // Keep at least one launcher alias enabled if a previous switch was interrupted.
        AppIconPrefUtil.ensureValidLauncherIcon(application)
        // apply compat style
        ThemePrefUtil.apply(application)

        // Enable diagnostic stack traces for Compose crashes
        Composer.setDiagnosticStackTraceMode(ComposeStackTraceMode.Auto)
        // Enable recomposition tracking ONLY in debug builds
        ComposeStabilityAnalyzer.setEnabled(BuildConfig.DEBUG)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
