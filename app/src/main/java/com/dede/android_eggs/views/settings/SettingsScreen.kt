@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.settings

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.android.launcher2.RocketLauncher
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.main.compose.DrawableImage
import com.dede.android_eggs.views.settings.compose.AboutGroup
import com.dede.android_eggs.views.settings.compose.ComponentManagerPref
import com.dede.android_eggs.views.settings.compose.ContactMeGroup
import com.dede.android_eggs.views.settings.compose.ContributeGroup
import com.dede.android_eggs.views.settings.compose.IconShapePref
import com.dede.android_eggs.views.settings.compose.IconVisualEffectsPref
import com.dede.android_eggs.views.settings.compose.LanguagePref
import com.dede.android_eggs.views.settings.compose.LanguagePrefUtil
import com.dede.android_eggs.views.settings.compose.RetainInRecentsPref
import com.dede.android_eggs.views.settings.compose.SettingDivider
import com.dede.android_eggs.views.settings.compose.SettingPref
import com.dede.android_eggs.views.settings.compose.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.ThemePref
import com.dede.android_eggs.views.settings.compose.TimelinePref
import kotlinx.coroutines.launch

@Preview(widthDp = 320)
@Composable
fun SettingsScreen(drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)) {
    val scope = rememberCoroutineScope()

    fun closeDrawer() {
        scope.launch {
            drawerState.close()
        }
    }

    BackHandler(drawerState.isOpen) {
        closeDrawer()
    }

    LocalEvent.receiver().register(action = SettingPrefUtil.ACTION_CLOSE_SETTING) {
        closeDrawer()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.End + WindowInsetsSides.Vertical),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.systemBars
                    .only(WindowInsetsSides.End + WindowInsetsSides.Top),
                title = {
                    Text(
                        text = stringResource(R.string.label_settings),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        closeDrawer()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 12.dp + contentPadding.calculateEndPadding(layoutDirection)
                )
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = contentPadding.calculateTopPadding() + 8.dp,
                        bottom = contentPadding.calculateBottomPadding() + 12.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemePref()

                IconShapePref()

                if (LanguagePrefUtil.isSupported()) {
                    LanguagePref()
                }

                IconVisualEffectsPref()

                SettingDivider()

                TimelinePref()

                val context = LocalContext.current
                SettingPref(
                    leadingIcon = Icons.Rounded.RocketLaunch,
                    title = stringResource(id = com.android.launcher2.R.string.dream_name),
                    trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
                    onClick = {
                        context.startActivity(Intent(context, RocketLauncher::class.java))
                    }
                )

                ComponentManagerPref()

                RetainInRecentsPref()

                SettingDivider()

                ContributeGroup()

                AboutGroup()

                ContactMeGroup()

                PoweredByCompose()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PoweredByCompose() {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column {
            Text(
                text = "Powered by",
                fontSize = TextUnit(8f, TextUnitType.Sp),
                fontFamily = FontFamily.SansSerif,
                lineHeight = TextUnit(8f, TextUnitType.Sp),
            )
            Text(
                text = "Compose",
                fontSize = TextUnit(14f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                lineHeight = TextUnit(12f, TextUnitType.Sp),
                fontFamily = FontFamily.SansSerif,
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        DrawableImage(
            res = R.drawable.ic_compose_logo,
            contentDescription = "Compose Logo",
            modifier = Modifier
                .width(24.dp)
                .align(Alignment.Bottom)
        )
    }
}
