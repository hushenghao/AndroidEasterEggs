package com.dede.android_eggs.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import android.provider.Settings
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.preferences.VersionPreference
import com.dede.android_eggs.ui.preferences.FontIconSwitchPreference
import com.dede.android_eggs.util.pref
import com.dede.basic.dp
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.EdgeToEdgeUtils
import java.util.*


private fun Preference.setup(prefs: SettingsPref<*>) {
    key = prefs.key
    isEnabled = prefs.isEnable()
    if (hasKey()) {
        setDefaultValue(prefs.getDefaultOption())
    }
}

private fun createFontIcon(context: Context, unicode: String): FontIconsDrawable {
    return FontIconsDrawable(context, unicode, 36f).apply {
        setPadding(12.dp, 6.dp, 0, 0)
    }
}

object SettingsPrefs {
    fun providePrefs(): List<SettingsPref<*>> = listOf(
        NightModePref(),
        IconShapePerf(),
        LanguagePerf(),
        EdgePref(),
        DynamicColorPref(),
        VersionPerf()
    )
}

abstract class SettingsPref<T>(open val key: String? = null) :
    Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    companion object {

        fun apply(context: Context) {
            NightModePref().apply(context)
            DynamicColorPref().apply(context)
        }
    }

    abstract fun getDefaultOption(): T
    open fun isEnable(): Boolean = true

    abstract fun onCreatePreference(context: Context): Preference

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        @Suppress("UNCHECKED_CAST")
        setOption(preference.context, newValue as T)
        return true
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        return true
    }

    fun apply(context: Context) {
        onApply(context, getOption(context))
    }

    open fun onApply(context: Context, value: T) {}

    open fun setOption(context: Context, newValue: T) {
        onApply(context, newValue)
    }

    open fun getOption(context: Context): T {
        return getDefaultOption()
    }
}

abstract class BoolSettingsPref(key: String, private val default: Boolean) :
    SettingsPref<Boolean>(key) {

    override fun getDefaultOption(): Boolean = default
    override fun getOption(context: Context): Boolean {
        return context.pref.getBoolean(key, getDefaultOption())
    }

    override fun setOption(context: Context, newValue: Boolean) {
        context.pref.edit(true) {
            putBoolean(key, newValue)
        }
        super.setOption(context, newValue)
    }
}

class EdgePref : BoolSettingsPref("key_pref_edge", true) {

    companion object {
        fun applyEdge(context: Context, window: Window) {
            applyEdge(window, EdgePref().getOption(context))
        }

        @SuppressLint("RestrictedApi")
        private fun applyEdge(window: Window, edgeToEdgeEnabled: Boolean) {
            EdgeToEdgeUtils.applyEdgeToEdge(window, edgeToEdgeEnabled)
        }
    }

    override fun onCreatePreference(context: Context): Preference {
        return SwitchPreferenceCompat(context).apply {
            setup(this@EdgePref)
            setTitle(R.string.pref_title_edge)
            isChecked = getOption(context)
            icon = createFontIcon(context, Icons.Outlined.fullscreen).apply {
                setPadding(9.dp, 4.5f.dp, 0, 0)
            }
            widgetLayoutResource = R.layout.layout_widget_material_switch
            if (isEnabled) {
                setSummaryOff(R.string.preference_off)
                setSummaryOn(R.string.preference_on)
            }
            onPreferenceChangeListener = this@EdgePref
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onApply(context: Context, value: Boolean) {
        ContextUtils.getActivity(context)?.recreate()
    }
}

class LanguagePerf : SettingsPref<String?>() {
    override fun getDefaultOption(): String? {
        return null
    }

    override fun isEnable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    override fun onCreatePreference(context: Context): Preference {
        return Preference(context).apply {
            setup(this@LanguagePerf)
            icon = createFontIcon(context, Icons.Outlined.language)
            setTitle(R.string.pref_title_language)
            summary = getLocalDisplayName(context)
            onPreferenceClickListener = this@LanguagePerf
        }
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val context = preference.context
            runCatching {
                context.startActivity(createActionAppLocaleSettingsIntent(context))
            }
        }
        return true
    }

    private fun getLocalDisplayName(context: Context): String? {
        var locales: LocaleList? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService<LocaleManager>()
            if (localeManager != null) {
                locales = localeManager.applicationLocales
                if (locales.isEmpty) {
                    locales = localeManager.systemLocales
                }
            }
            if (locales != null && !locales.isEmpty) {
                return locales.get(0).displayName
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locales = context.resources.configuration.locales
            if (!locales.isEmpty) {
                return locales.get(0).displayName
            }
        }
        @Suppress("DEPRECATION")
        val locale = context.resources.configuration.locale
        return locale.displayName
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun createActionAppLocaleSettingsIntent(context: Context): Intent = Intent(
        Settings.ACTION_APP_LOCALE_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

}

class VersionPerf : SettingsPref<String?>() {

    override fun getDefaultOption(): String? = null

    override fun onCreatePreference(context: Context): Preference {
        return VersionPreference(context, null).apply {
            icon = createFontIcon(context, Icons.Outlined.info)
        }
    }
}

class IconShapePerf : SettingsPref<String>(IconShapeOverride.KEY_PREFERENCE) {

    override fun getDefaultOption(): String = ""
    override fun isEnable(): Boolean = IconShapeOverride.isEnabled()

    override fun onCreatePreference(context: Context): Preference {
        return ListPreference(context).apply {
            setup(this@IconShapePerf)
            icon = createFontIcon(context, Icons.Outlined.rounded_corner)
            setTitle(R.string.icon_shape_override_label)
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            entries = context.resources.getStringArray(R.array.icon_shape_override_paths_names)
            entryValues = context.resources.getStringArray(R.array.icon_shape_override_paths_values)
            IconShapeOverride.handlePreferenceUi(this)
        }
    }

    override fun getOption(context: Context): String {
        return IconShapeOverride.getAppliedValue(context)
    }
}

class DynamicColorPref :
    BoolSettingsPref("key_dynamic_color", DynamicColors.isDynamicColorAvailable()),
    DynamicColors.Precondition, DynamicColors.OnAppliedCallback {

    override fun isEnable(): Boolean {
        return DynamicColors.isDynamicColorAvailable()
    }

    override fun onCreatePreference(context: Context): Preference {
        return SwitchPreferenceCompat(context).apply {
            setup(this@DynamicColorPref)
            setTitle(R.string.pref_title_dynamic_color)
            isChecked = getOption(context)
            icon = createFontIcon(context, Icons.Outlined.palette)
            widgetLayoutResource = R.layout.layout_widget_material_switch
            if (isEnabled) {
                setSummaryOff(R.string.preference_off)
                setSummaryOn(R.string.preference_on)
            }
            onPreferenceChangeListener = this@DynamicColorPref
        }
    }

    override fun onApply(context: Context, value: Boolean) {
        DynamicColors.applyToActivitiesIfAvailable(
            context.applicationContext as Application,
            DynamicColorsOptions.Builder()
                .setPrecondition(this)
                .setOnAppliedCallback(this)
                .build()
        )
        @Suppress("RestrictedApi")
        ContextUtils.getActivity(context)?.recreate()
    }

    override fun shouldApplyDynamicColors(activity: Activity, theme: Int): Boolean {
        if (activity is AppCompatActivity) {
            return getOption(activity)
        }
        return false
    }

    override fun onApplied(activity: Activity) {
        HarmonizedColors.applyToContextIfAvailable(
            activity, HarmonizedColorsOptions.createMaterialDefaults()
        )
    }
}

class NightModePref : BoolSettingsPref("key_night_mode", false) {

    companion object {
        fun isSystemNightMode(context: Context): Boolean {
            return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
        }
    }

    override fun onCreatePreference(context: Context): Preference {
        return FontIconSwitchPreference(context, null).apply {
            setup(this@NightModePref)
            setTitle(R.string.pref_title_theme)
            icon = createFontIcon(context, Icons.Outlined.brightness_6)
            setSummaryOff(R.string.summary_theme_follow_system)
            setSummaryOn(R.string.summary_theme_dark_mode)
            switchTextOff = Icons.Outlined.brightness_auto
            switchTextOn = Icons.Outlined.brightness_4
            isChecked = getOption(context)
            onPreferenceChangeListener = this@NightModePref
        }
    }

    override fun onApply(context: Context, value: Boolean) {
        val mode = if (value) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        if (mode == AppCompatDelegate.getDefaultNightMode()) {
            return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

}