package com.dede.android_eggs.views.main.compose

import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.fragment.app.FragmentManager


val LocalFragmentManager = staticCompositionLocalOf<FragmentManager?> {
    Log.w("LocalFragmentManager", "CompositionLocal LocalFragmentManager not present")
    null
}

