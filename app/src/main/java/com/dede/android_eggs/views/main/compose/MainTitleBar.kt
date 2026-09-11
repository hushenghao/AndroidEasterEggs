@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.R
import com.dede.android_eggs.composable.appbar.HazeAppBar
import com.dede.android_eggs.ui.composes.icons.rounded.SettingsOutline
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringsR

@Composable
@Preview
fun MainTitleBar(
    modifier: Modifier = Modifier,
    searchBarState: BottomSearchBarState = rememberBottomSearchBarState(),
    drawerState: DrawerState? = null,
    showSettingsAction: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    HazeAppBar(
        modifier = modifier,
        title = stringResource(R.string.app_name),
        navigationIcon = {
            AnimatedVisibility(
                visible = !searchBarState.visible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                IconButton(
                    onClick = {
                        searchBarState.open()
                        if (drawerState != null && drawerState.isOpen) {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(android.R.string.search_go),
                    )
                }
            }
        },
        actions = {
            if (showSettingsAction) {
                val drawerOpen = drawerState?.currentValue == DrawerValue.Open
                Crossfade(drawerOpen) { isOpen ->
                    if (isOpen) {
                        // close drawer
                        IconButton(
                            onClick = {
                                if (drawerState != null && drawerState.isOpen) {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = stringResource(StringsR.string.label_back),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    } else {
                        // open drawer
                        IconButton(
                            onClick = {
                                searchBarState.close()
                                scope.launch {
                                    drawerState?.open()
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SettingsOutline,
                                contentDescription = stringResource(StringsR.string.label_settings),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        },
    )
}
