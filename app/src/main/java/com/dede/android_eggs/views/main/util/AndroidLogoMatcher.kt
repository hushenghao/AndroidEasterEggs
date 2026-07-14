package com.dede.android_eggs.views.main.util

import android.util.SparseIntArray
import androidx.annotation.DrawableRes
import androidx.core.util.set
import com.dede.basic.globalContext
import com.dede.basic.provider.EasterEgg
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

object AndroidLogoMatcher {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PureEasterEggListEntryPoint {
        fun pureEasterEggList(): List<@JvmSuppressWildcards EasterEgg>
    }

    private val cache = SparseIntArray()

    private val easterEggs: List<EasterEgg>

    init {
        val entryPoint = EntryPointAccessors
            .fromApplication(globalContext, PureEasterEggListEntryPoint::class.java)
        easterEggs = entryPoint.pureEasterEggList()
    }

    fun findEasterEggByFullApiLevel(fullApiLevel: Int): EasterEgg? {
        return easterEggs.find { fullApiLevel in it.fullApiLevelRange }
    }

    @DrawableRes
    fun findAndroidLogoByFullApiLevel(fullApiLevel: Int): Int {
        if (cache.indexOfKey(fullApiLevel) >= 0) {
            return cache[fullApiLevel]
        }
        val easterEgg = findEasterEggByFullApiLevel(fullApiLevel)
            ?: throw IllegalArgumentException("Not found Android logo res, fullApiLevel: $fullApiLevel")
        cache[fullApiLevel] = easterEgg.iconRes
        return easterEgg.iconRes
    }

}
