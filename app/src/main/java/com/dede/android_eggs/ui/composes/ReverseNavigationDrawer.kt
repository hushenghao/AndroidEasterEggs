@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.ui.composes

import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * Navigation drawers mirrored to the end side regardless of layout direction:
 * the drawer sheet stays on the right for LTR locales and on the left for RTL.
 */
@Composable
fun ReverseModalNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit
) {
    ReverseLayoutDirection {
        ModalNavigationDrawer(
            drawerContent = {
                ReverseLayoutDirection {
                    drawerContent()
                }
            },
            modifier = modifier,
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            scrimColor = scrimColor,
            content = {
                ReverseLayoutDirection {
                    content()
                }
            }
        )
    }
}

@Composable
fun ReverseDismissibleNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    ReverseLayoutDirection {
        DismissibleNavigationDrawer(
            drawerContent = {
                ReverseLayoutDirection {
                    drawerContent()
                }
            },
            modifier = modifier,
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            content = {
                ReverseLayoutDirection {
                    content()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReversePermanentNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ReverseLayoutDirection {
        PermanentNavigationDrawer(
            drawerContent = {
                ReverseLayoutDirection {
                    drawerContent()
                }
            },
            modifier = modifier,
            content = {
                ReverseLayoutDirection {
                    content()
                }
            }
        )
    }
}

@Composable
private fun ReverseLayoutDirection(content: @Composable () -> Unit) {
    val reverseDirection = when (LocalLayoutDirection.current) {
        LayoutDirection.Rtl -> LayoutDirection.Ltr
        LayoutDirection.Ltr -> LayoutDirection.Rtl
    }
    CompositionLocalProvider(LocalLayoutDirection provides reverseDirection) {
        content()
    }
}