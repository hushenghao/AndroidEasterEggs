package com.dede.android_eggs.egg_stats

import android.content.Context
import androidx.startup.Initializer

internal class EggStatsInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        EggStatsRepository.from(context).preload()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
