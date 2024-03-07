@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.settings.compose.AboutGroup
import com.dede.android_eggs.views.settings.compose.ComponentManagerPref
import com.dede.android_eggs.views.settings.compose.DynamicColorPref
import com.dede.android_eggs.views.settings.compose.IconShapePref
import com.dede.android_eggs.views.settings.compose.IconVisualEffectsPref
import com.dede.android_eggs.views.settings.compose.LanguagePref
import com.dede.android_eggs.views.settings.compose.RetainInRecentsPref
import com.dede.android_eggs.views.settings.compose.SettingPref
import com.dede.android_eggs.views.settings.compose.ThemePref
import com.dede.android_eggs.views.settings.compose.TimelinePref
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    LocalEvent.receiver().register(action = SettingPref.ACTION_CLOSE_SETTING) {
        scope.launch {
            drawerState.close()
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.label_settings),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
            }
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemePref()

            IconShapePref()

            LanguagePref()

            DynamicColorPref()

            IconVisualEffectsPref()

            TimelinePref()

            ComponentManagerPref()

            RetainInRecentsPref()

            AboutGroup()

            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}


