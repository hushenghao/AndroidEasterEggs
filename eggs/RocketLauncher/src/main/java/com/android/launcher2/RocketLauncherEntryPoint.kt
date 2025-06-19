package com.android.launcher2

import com.dede.basic.provider.EasterEgg
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface RocketLauncherEntryPoint {
    val easterEggs: List<EasterEgg>
}
