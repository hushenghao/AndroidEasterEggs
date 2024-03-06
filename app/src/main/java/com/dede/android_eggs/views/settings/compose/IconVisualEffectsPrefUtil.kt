package com.dede.android_eggs.views.settings.compose

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent


object IconVisualEffectsPrefUtil {
    const val ACTION_CHANGED = "com.dede.android_eggs.IconVisualEffectsChanged"
    const val KEY_ICON_VISUAL_EFFECTS = "pref_key_icon_visual_effects"

    private const val DEFAULT = SettingPref.OFF

    fun isEnable(context: Context): Boolean {
        return SettingPref.getValue(
            context, KEY_ICON_VISUAL_EFFECTS, DEFAULT
        ) == SettingPref.ON
    }
}

@Composable
fun IconVisualEffectsPref() {
    var value by rememberPrefIntState(
        IconVisualEffectsPrefUtil.KEY_ICON_VISUAL_EFFECTS,
        SettingPref.OFF
    )
    val context = LocalContext.current
    SwitchPref(
        leadingIcon = imageVectorIcon(
            imageVector = Icons.Rounded.Animation,
            contentDescription = stringResource(R.string.pref_title_icon_visual_effects)
        ),
        title = stringResource(R.string.pref_title_icon_visual_effects),
        value = value,
        onCheckedChange = {
            value = if (it) SettingPref.ON else SettingPref.OFF
            with(LocalEvent.poster(context)) {
                if (it) {
                    post(SettingPref.ACTION_CLOSE_SETTING)
                }
                post(
                    IconVisualEffectsPrefUtil.ACTION_CHANGED,
                    bundleOf(SettingPref.EXTRA_VALUE to it)
                )
            }
        }
    )
}