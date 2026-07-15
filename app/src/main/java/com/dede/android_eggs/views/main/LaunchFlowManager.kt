package com.dede.android_eggs.views.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.navigation.DeepLink
import com.dede.android_eggs.navigation.EasterEggsDestination.AnimatorDisabledAlertDialog
import com.dede.android_eggs.navigation.EasterEggsDestination.WelcomeDialog
import com.dede.android_eggs.navigation.Navigator
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy
import com.dede.basic.Utils
import com.dede.android_eggs.views.main.compose.AnimatorDisabledAlertDialog as AnimatorAlert

private enum class FlowState {
    Welcome,
    AnimatorAlert,
    DeepLink,
    Completed,
    ;

    val next: FlowState
        get() {
            val nextOrdinal = ordinal + 1
            return if (nextOrdinal < entries.size) entries[nextOrdinal] else this
        }
}

@Composable
fun LaunchFlowEffect(
    navigator: Navigator,
    currentRoute: NavKey?,
) {
    val context = LocalContext.current
    var flowState by rememberSaveable { mutableStateOf(FlowState.Welcome) }

    LaunchedEffect(flowState, currentRoute) {
        when (flowState) {
            FlowState.Welcome -> {
                if (currentRoute == WelcomeDialog) return@LaunchedEffect
                if (!isAgreedPrivacyPolicy(context)) {
                    navigator.navigate(WelcomeDialog, true)
                }
            }
            FlowState.AnimatorAlert -> {
                if (currentRoute == AnimatorDisabledAlertDialog) return@LaunchedEffect
                if (!Utils.areAnimatorEnabled(context)
                    && !AnimatorAlert.isDontShowAgain(context)
                ) {
                    navigator.navigate(AnimatorDisabledAlertDialog, true)
                }
            }
            FlowState.DeepLink -> {
                DeepLink.handleNavKey(navigator)
            }
            FlowState.Completed -> {
                return@LaunchedEffect
            }
        }

        flowState = flowState.next
    }
}
