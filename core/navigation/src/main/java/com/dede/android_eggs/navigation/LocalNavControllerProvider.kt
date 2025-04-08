package com.dede.android_eggs.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.dede.android_eggs.local_provider.noLocalProvidedFor

val LocalNavController = compositionLocalOf<NavHostController> {
    noLocalProvidedFor("LocalNavController")
}
