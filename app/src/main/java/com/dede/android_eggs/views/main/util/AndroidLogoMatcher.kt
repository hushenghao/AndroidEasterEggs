package com.dede.android_eggs.views.main.util

import android.util.SparseIntArray
import androidx.annotation.DrawableRes
import androidx.core.util.set
import com.dede.basic.provider.EasterEgg
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidLogoMatcher @Inject constructor(
    private val easterEggs: List<@JvmSuppressWildcards EasterEgg>,
) {

    private val cache = SparseIntArray()

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
