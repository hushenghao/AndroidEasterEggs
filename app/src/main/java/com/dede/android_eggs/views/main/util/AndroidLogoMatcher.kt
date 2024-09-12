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

    fun findEasterEgg(apiLevel: Int): EasterEgg? {
        return easterEggs.find { apiLevel in it.apiLevelRange }
    }

    @DrawableRes
    fun findAndroidLogo(apiLevel: Int): Int {
        if (cache.indexOfKey(apiLevel) >= 0) {
            return cache[apiLevel]
        }
        val easterEgg = findEasterEgg(apiLevel)
            ?: throw IllegalArgumentException("Not found Android logo res, level: $apiLevel")
        cache[apiLevel] = easterEgg.iconRes
        return easterEgg.iconRes
    }

}
