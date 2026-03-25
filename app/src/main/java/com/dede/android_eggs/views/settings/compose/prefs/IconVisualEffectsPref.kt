package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.main.compose.LocalDrawerState
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.SwitchIntPref
import com.dede.basic.bundleBuilder
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringsR

object IconVisualEffectsPrefUtil {
    const val ACTION_CHANGED = "com.dede.android_eggs.IconVisualEffectsChanged"
    const val KEY_ICON_VISUAL_EFFECTS = "pref_key_icon_visual_effects"

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun isEnable(context: Context): Boolean {
        return isSupported() && SettingPrefUtil.getValue(
            context, KEY_ICON_VISUAL_EFFECTS, SettingPrefUtil.OFF
        ) == SettingPrefUtil.ON
    }
}

@Composable
fun IconVisualEffectsPref() {
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()
    SwitchIntPref(
        key = IconVisualEffectsPrefUtil.KEY_ICON_VISUAL_EFFECTS,
        leadingIcon = Icons.Rounded.Animation,
        title = stringResource(StringsR.string.pref_title_icon_visual_effects),
        default = SettingPrefUtil.OFF,
        onCheckedChange = {
            if (it == SettingPrefUtil.ON) {
                scope.launch {
                    drawerState.close()
                }
            }
            with(LocalEvent.poster()) {

                val bundle = bundleBuilder {
                    putBoolean(SettingPrefUtil.EXTRA_VALUE, (it == SettingPrefUtil.ON))
                }
                post(IconVisualEffectsPrefUtil.ACTION_CHANGED, bundle)
            }
        }
    )
}
