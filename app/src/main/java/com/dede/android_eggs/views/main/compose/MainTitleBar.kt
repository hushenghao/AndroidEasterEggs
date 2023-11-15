@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.SettingsFragment


private const val TAG_SETTINGS = "Settings"

@Composable
@Preview
fun MainTitleBar() {
    val fm = LocalFragmentManager.current

    var startRotate by rememberSaveable { mutableStateOf(false) }
    val rotateAnim by animateFloatAsState(
        targetValue = if (startRotate) 360f else 0f,
        animationSpec = tween(500),
        label = "setting_icon_rotate",
    )

    if (startRotate) {
        val restored = fm?.findFragmentByTag(TAG_SETTINGS) as? SettingsFragment
        if (restored != null) {
            restored.onPreDismiss = {
                startRotate = false
            }
        }
    }

    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        actions = {
            IconButton(
                onClick = {
                    if (fm == null) return@IconButton
                    SettingsFragment().apply {
                        onPreDismiss = {
                            startRotate = false
                        }
                    }.show(fm, TAG_SETTINGS)
                    startRotate = true
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.label_settings),
                    modifier = Modifier.rotate(rotateAnim)
                )
            }
        }
    )
}