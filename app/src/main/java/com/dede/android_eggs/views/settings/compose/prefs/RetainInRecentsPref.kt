package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.main.util.EggActionHelp
import com.dede.android_eggs.views.settings.compose.basic.SwitchPref
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefBoolState
import com.dede.android_eggs.resources.R as StringsR

object RetainInRecentsPrefUtil {
    const val KEY_RETAIN_IN_RECENTS = "key_retain_in_recents"

    fun isRetainInRecentsEnabled(context: Context): Boolean {
        return context.pref.getBoolean(KEY_RETAIN_IN_RECENTS, false)
    }
}

@Composable
fun RetainInRecentsPref() {
    val context = LocalContext.current
    SwitchPref(
        state = rememberPrefBoolState(
            RetainInRecentsPrefUtil.KEY_RETAIN_IN_RECENTS,
            false
        ),
        leadingIcon = Icons.Rounded.ViewCarousel,
        title = stringResource(id = StringsR.string.pref_retain_recent_egg_tasks),
        desc = stringResource(id = StringsR.string.pref_summary_retain_recent_egg_tasks),
        onCheckedChange = { checked ->
            if (!checked) {
                EggActionHelp.cleanupProxyTasks(context)
            }
        }
    )
}
