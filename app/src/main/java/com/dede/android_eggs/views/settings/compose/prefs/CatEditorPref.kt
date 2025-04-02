package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.cat_editor.CatEditorSheetDialog
import com.dede.android_eggs.ui.composes.icons.rounded.Cat
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.android_eggs.resources.R as StringR

@Composable
fun CatEditorPref() {
    val showSheetState = remember { mutableStateOf(false) }

    CatEditorSheetDialog(showSheetState)

    SettingPref(
        leadingIcon = Icons.Rounded.Cat,
        title = stringResource(StringR.string.cat_editor),
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            showSheetState.value = true
        }
    )
}
