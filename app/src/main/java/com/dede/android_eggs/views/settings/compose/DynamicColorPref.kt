package com.dede.android_eggs.views.settings.compose

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.compose.DynamicColorPrefUtil.DEFAULT
import com.dede.android_eggs.views.settings.compose.DynamicColorPrefUtil.KEY_DYNAMIC_COLOR
import com.dede.android_eggs.views.theme.isDynamicEnable
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions

object DynamicColorPrefUtil {

    val DEFAULT =
        if (DynamicColors.isDynamicColorAvailable()) SettingPref.ON else SettingPref.OFF
    const val KEY_DYNAMIC_COLOR = "pref_key_dynamic_color"
    const val ACTION_DYNAMIC_COLOR_CHANGED = "ACTION_DYNAMIC_COLOR_CHANGED"

    fun isDynamicEnable(context: Context): Boolean {
        return SettingPref.getValue(context, KEY_DYNAMIC_COLOR, DEFAULT) == SettingPref.ON
    }

    fun apply(context: Context) {
        val callback = Callback()
        DynamicColors.applyToActivitiesIfAvailable(
            context.applicationContext as Application,
            DynamicColorsOptions.Builder()
                .setPrecondition(callback)
                .setOnAppliedCallback(callback)
                .build()
        )
    }

    private class Callback : DynamicColors.Precondition, DynamicColors.OnAppliedCallback {
        override fun shouldApplyDynamicColors(activity: Activity, theme: Int): Boolean {
            if (activity is AppCompatActivity) {
                return isDynamicEnable(activity)
            }
            return false
        }

        override fun onApplied(activity: Activity) {
            HarmonizedColors.applyToContextIfAvailable(
                activity, HarmonizedColorsOptions.createMaterialDefaults()
            )
        }

    }
}

@Composable
fun DynamicColorPref() {
    SwitchIntPref(
        key = KEY_DYNAMIC_COLOR,
        default = DEFAULT,
        leadingIcon = Icons.Rounded.Palette,
        title = stringResource(R.string.pref_title_dynamic_color),
        onCheckedChange = {
            isDynamicEnable = it == SettingPref.ON
        }
    )
}