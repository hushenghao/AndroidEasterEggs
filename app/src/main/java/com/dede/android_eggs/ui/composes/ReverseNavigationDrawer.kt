package com.dede.android_eggs.ui.composes

import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

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