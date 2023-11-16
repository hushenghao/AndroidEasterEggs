package com.dede.android_eggs.views.main.compose

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.fragment.app.FragmentManager
import com.dede.android_eggs.views.main.EasterEggLogoSensorMatrixConvert


val LocalFragmentManager = staticCompositionLocalOf<FragmentManager?> {
    Log.w("LocalFragmentManager", "CompositionLocal LocalFragmentManager not present")
    null
}

val LocalHost = staticCompositionLocalOf<Activity?> { null }

val LocalEasterEggLogoSensor = staticCompositionLocalOf<EasterEggLogoSensorMatrixConvert> {
    throw IllegalStateException("CompositionLocal LocalEasterEggLogoSensor not present")
}

