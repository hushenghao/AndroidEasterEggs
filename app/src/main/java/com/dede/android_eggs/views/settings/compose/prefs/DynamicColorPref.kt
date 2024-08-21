package com.dede.android_eggs.views.settings.compose.prefs

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil.DEFAULT
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil.KEY_DYNAMIC_COLOR
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.SwitchOption
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefIntState
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil.DEFAULT
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil.KEY_DYNAMIC_COLOR
import com.dede.android_eggs.views.theme.isDynamicColorEnable
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions

object DynamicColorPrefUtil {

    val DEFAULT =
        if (DynamicColors.isDynamicColorAvailable()) SettingPrefUtil.ON else SettingPrefUtil.OFF
    const val KEY_DYNAMIC_COLOR = "pref_key_dynamic_color"
    const val ACTION_DYNAMIC_COLOR_CHANGED = "ACTION_DYNAMIC_COLOR_CHANGED"

    fun isSupported(): Boolean {
        return DynamicColors.isDynamicColorAvailable()
    }

    fun isDynamicColorEnable(context: Context): Boolean {
        return SettingPrefUtil.getValue(context, KEY_DYNAMIC_COLOR, DEFAULT) == SettingPrefUtil.ON
    }

    fun apply(context: Context) {
        if (!isSupported()) return

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

@Preview
@Composable
fun DynamicColorPref() {
    var intState by rememberPrefIntState(key = KEY_DYNAMIC_COLOR, default = DEFAULT)
    SwitchOption(
        leadingIcon = imageVectorIconBlock(
            imageVector = Icons.Rounded.Palette,
            contentDescription = stringResource(R.string.pref_title_dynamic_color),
        ),
        title = stringResource(R.string.pref_title_dynamic_color),
        value = intState == SettingPrefUtil.ON,
        shape = OptionShapes.borderShape,
        onCheckedChange = {
            intState = if (it) SettingPrefUtil.ON else SettingPrefUtil.OFF
            isDynamicColorEnable = intState == SettingPrefUtil.ON
        }
    )
}