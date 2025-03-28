package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.dede.android_eggs.cat_editor.CatEditorSheetDialog
import com.dede.android_eggs.views.settings.compose.basic.SettingPref

@Composable
fun CatEditorPref() {
    val showSheetState = remember { mutableStateOf(false) }

    CatEditorSheetDialog(showSheetState)

    SettingPref(
        leadingIcon = Icons.Rounded.Pets,
        title = "Cat Editor",
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            showSheetState.value = true
        }
    )
}
