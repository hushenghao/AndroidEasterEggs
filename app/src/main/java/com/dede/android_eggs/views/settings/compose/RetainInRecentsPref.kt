package com.dede.android_eggs.views.settings.compose

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.util.pref

private const val KEY_RETAIN_IN_RECENTS = "key_retain_in_recents"

fun isRetainInRecentsEnabled(context: Context): Boolean {
    return context.pref.getBoolean(KEY_RETAIN_IN_RECENTS, false)
}

@Composable
fun RetainInRecentsPref() {
    var state by rememberPrefBoolState(key = KEY_RETAIN_IN_RECENTS, default = false)
    SwitchPref(
        key = KEY_RETAIN_IN_RECENTS,
        leadingIcon = Icons.Rounded.ViewCarousel,
        title = stringResource(id = R.string.pref_retain_recent_egg_tasks),
        desc = stringResource(id = R.string.pref_summary_retain_recent_egg_tasks),
        default = state
    ) {
        state = it
    }
}