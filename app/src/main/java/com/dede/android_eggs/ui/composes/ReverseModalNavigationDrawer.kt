package com.dede.android_eggs.ui.composes

import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

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
private fun ReverseLayoutDirection(content: @Composable () -> Unit) {
    val reverseDirection = when (LocalLayoutDirection.current) {
        LayoutDirection.Rtl -> LayoutDirection.Ltr
        LayoutDirection.Ltr -> LayoutDirection.Rtl
    }
    CompositionLocalProvider(LocalLayoutDirection provides reverseDirection) {
        content()
    }
}