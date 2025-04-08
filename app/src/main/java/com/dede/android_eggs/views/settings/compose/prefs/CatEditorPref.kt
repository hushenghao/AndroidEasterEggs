package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.cat_editor.CatEditorScreen
import com.dede.android_eggs.navigation.LocalNavController
import com.dede.android_eggs.ui.composes.icons.rounded.Cat
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.android_eggs.resources.R as StringR

@Composable
fun CatEditorPref() {
    val navController = LocalNavController.current
    SettingPref(
        leadingIcon = Icons.Rounded.Cat,
        title = stringResource(StringR.string.cat_editor),
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            navController.navigate(CatEditorScreen.route)
        }
    )
}
