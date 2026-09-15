package com.dede.android_eggs.views.settings.backup

import android.content.Context
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.views.settings.compose.prefs.AppIconPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.LanguagePrefUtil

/**
 * Restores the settings a fresh install starts with.
 *
 * [AppSettings] covers the default preferences file, while the launcher icon
 * lives in the `PackageManager` component state and the language in
 * `AppCompatDelegate`, so this is not a single call. Saved cats and widget
 * configurations are user data rather than settings and are left alone.
 */
internal object DefaultSettings {

    fun restore(context: Context) {
        AppSettings.reset(context)
        AppIconPrefUtil.switchIcon(context, AppIconPrefUtil.defaultIcon)
        LanguagePrefUtil.setApplicationLocalesValue(LanguagePrefUtil.SYSTEM)
    }
}
