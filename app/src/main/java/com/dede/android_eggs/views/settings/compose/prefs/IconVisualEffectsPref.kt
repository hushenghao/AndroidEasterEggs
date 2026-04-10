package com.dede.android_eggs.views.settings.compose.prefs

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.views.main.compose.LocalDrawerState
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil.setBooleanValue
import com.dede.android_eggs.views.settings.compose.basic.SwitchIntPref
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringsR

object IconVisualEffectsPrefUtil {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}

@Composable
fun IconVisualEffectsPref() {
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()
    SwitchIntPref(
        key = SettingPrefUtil.KEY_ICON_VISUAL_EFFECTS,
        leadingIcon = Icons.Rounded.Animation,
        title = stringResource(StringsR.string.pref_title_icon_visual_effects),
        default = SettingPrefUtil.OFF,
        onCheckedChange = {
            if (it == SettingPrefUtil.ON) {
                scope.launch {
                    drawerState.close()
                }
            }
            SettingPrefUtil.iconVisualEffectsState.setBooleanValue(it)
        }
    )
}
