@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentManager
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.SettingsFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val TAG_SETTINGS = "Settings"

@Composable
@Preview
fun MainTitleBar(
    scrollBehavior: TopAppBarScrollBehavior = pinnedScrollBehavior(),
    searchBarVisibleState: MutableState<Boolean> = mutableStateOf(false)
) {
    val fm: FragmentManager? = LocalFragmentManager.currentOutInspectionMode
    var searchBarVisible by searchBarVisibleState

    val scope = rememberCoroutineScope()
    var startRotate by rememberSaveable { mutableStateOf(false) }
    val rotateAnim by animateFloatAsState(
        targetValue = if (startRotate) 360f else 0f,
        animationSpec = tween(500),
        label = "setting_icon_rotate",
    )

    fun showSettings() {
        if (fm == null) return
        SettingsFragment().apply {
            onPreDismiss = {
                startRotate = false
            }
        }.show(fm, TAG_SETTINGS)
        startRotate = true
    }

    if (startRotate) {
        val restored = fm?.findFragmentByTag(TAG_SETTINGS) as? SettingsFragment
        if (restored != null) {
            restored.onPreDismiss = {
                startRotate = false
            }
        }
    }

    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            AnimatedVisibility(
                visible = !searchBarVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                IconButton(
                    onClick = {
                        // show searchBar
                        searchBarVisible = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.label_search_hint),
                    )
                }
            }

            IconButton(
                onClick = {
                    scope.launch {
                        if (searchBarVisible) {
                            // hide searchBar
                            searchBarVisible = false
                            // await searchBar dismiss
                            delay(200)
                        }
                        showSettings()
                    }
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
