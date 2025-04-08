package com.dede.android_eggs.views.main.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.dede.android_eggs.local_provider.noLocalProvidedFor
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert

val LocalEasterEggLogoSensor = compositionLocalOf<EasterEggLogoSensorMatrixConvert> {
    noLocalProvidedFor("LocalEasterEggLogoSensor")
}

val LocalKonfettiState = staticCompositionLocalOf {
    mutableStateOf(false)
}
