package com.dede.android_eggs.views.main.compose

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.fragment.app.FragmentManager
import com.dede.android_eggs.views.main.EasterEggsActivity


val LocalFragmentManager = staticCompositionLocalOf<FragmentManager?> {
    Log.w("LocalFragmentManager", "CompositionLocal LocalFragmentManager not present")
    null
}

val LocalHost = staticCompositionLocalOf<Activity?> { null }

val LocalEasterEggLogoSensor = staticCompositionLocalOf<EasterEggsActivity.Sensor> {
    throw IllegalStateException("CompositionLocal LocalEasterEggLogoSensor not present")
}

