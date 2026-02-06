package com.dede.android_eggs.navigation

import androidx.compose.runtime.compositionLocalOf
import com.dede.android_eggs.local_provider.noLocalProvidedFor

val LocalNavigator = compositionLocalOf<Navigator> {
    noLocalProvidedFor("LocalNavigator")
}
