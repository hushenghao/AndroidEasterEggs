package com.dede.android_eggs.views.settings.compose

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent


object IconVisualEffectsPrefUtil {
    const val ACTION_CHANGED = "com.dede.android_eggs.IconVisualEffectsChanged"
    const val KEY_ICON_VISUAL_EFFECTS = "pref_key_icon_visual_effects"

    fun isEnable(context: Context): Boolean {
        return SettingPrefUtil.getValue(
            context, KEY_ICON_VISUAL_EFFECTS, SettingPrefUtil.OFF
        ) == SettingPrefUtil.ON
    }
}

@Composable
fun IconVisualEffectsPref() {
    val context = LocalContext.current
    SwitchIntPref(
        key = IconVisualEffectsPrefUtil.KEY_ICON_VISUAL_EFFECTS,
        leadingIcon = Icons.Rounded.Animation,
        title = stringResource(R.string.pref_title_icon_visual_effects),
        default = SettingPrefUtil.OFF,
        onCheckedChange = {
            with(LocalEvent.poster(context)) {
                if (it == SettingPrefUtil.ON) {
                    post(SettingPrefUtil.ACTION_CLOSE_SETTING)
                }
                post(
                    IconVisualEffectsPrefUtil.ACTION_CHANGED,
                    bundleOf(SettingPrefUtil.EXTRA_VALUE to (it == SettingPrefUtil.ON))
                )
            }
        }
    )
}