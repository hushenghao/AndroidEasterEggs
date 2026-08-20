package com.dede.android_eggs.views.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.composable.appbar.HazeScaffold
import com.dede.android_eggs.keep_android_open.KeepAndroidOpen
import com.dede.android_eggs.util.SplitUtils
import com.dede.android_eggs.util.isVivo
import com.dede.android_eggs.views.settings.compose.basic.SettingDivider
import com.dede.android_eggs.views.settings.compose.groups.AboutGroup
import com.dede.android_eggs.views.settings.compose.groups.ContactMeGroup
import com.dede.android_eggs.views.settings.compose.groups.ContributeGroup
import com.dede.android_eggs.views.settings.compose.prefs.AppIconPref
import com.dede.android_eggs.views.settings.compose.prefs.CatEditorPref
import com.dede.android_eggs.views.settings.compose.prefs.ColorSourcePref
import com.dede.android_eggs.views.settings.compose.prefs.ComponentManagerPref
import com.dede.android_eggs.views.settings.compose.prefs.DataBackupPref
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePref
import com.dede.android_eggs.views.settings.compose.prefs.IconVisualEffectsPref
import com.dede.android_eggs.views.settings.compose.prefs.IconVisualEffectsPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.LanguagePref
import com.dede.android_eggs.views.settings.compose.prefs.LanguagePrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.RetainInRecentsPref
import com.dede.android_eggs.views.settings.compose.prefs.RocketLauncherPref
import com.dede.android_eggs.views.settings.compose.prefs.SnapshotPref
import com.dede.android_eggs.views.settings.compose.prefs.ThemePref
import com.dede.android_eggs.views.settings.compose.prefs.TimelinePref
import com.dede.android_eggs.views.settings.compose.prefs.WidgetsPref
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringsR

@Preview(widthDp = 320)
@Composable
fun SettingsScreen(drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)) {
    val scope = rememberCoroutineScope()
    val windowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout)
    HazeScaffold(
        title = stringResource(StringsR.string.label_settings),
        onBackClick = {
            scope.launch {
                drawerState.close()
            }
        },
        contentWindowInsets = windowInsets
            .only(WindowInsetsSides.End + WindowInsetsSides.Vertical),
        topBarWindowInsets = windowInsets.only(WindowInsetsSides.End + WindowInsetsSides.Top),
    ) { contentPadding ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 12.dp + contentPadding.calculateEndPadding(layoutDirection),
                )// 1. horizontal padding
                .verticalScroll(rememberScrollState())// 2. scrollable
                .padding(// 3. vertical padding
                    top = contentPadding.calculateTopPadding() + 8.dp,
                    bottom = contentPadding.calculateBottomPadding() + 12.dp
                )
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val context = LocalContext.current
            ThemePref()

            ColorSourcePref()

            AppIconPref()

            IconShapePref()

            if (LanguagePrefUtil.isSupported()) {
                LanguagePref()
            }

            if (IconVisualEffectsPrefUtil.isSupported()) {
                IconVisualEffectsPref()
            }

            SettingDivider()

            SnapshotPref()

            TimelinePref()

            CatEditorPref()

            RocketLauncherPref()

            WidgetsPref()

            ComponentManagerPref()

            // Hidden on VIVO: its recents merges all of an app's tasks into one
            // card regardless of task/process/affinity, so this feature can't
            // work there (issue #935).
            if (!SplitUtils.isActivityEmbedded(context) && !isVivo()) {
                RetainInRecentsPref()
            }

            DataBackupPref()

            SettingDivider()

            AboutGroup()

            ContributeGroup()

            ContactMeGroup()

            SettingDivider()

            KeepAndroidOpen()
        }
    }
}
