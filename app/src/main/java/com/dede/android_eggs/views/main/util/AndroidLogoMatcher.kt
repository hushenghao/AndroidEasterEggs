package com.dede.android_eggs.views.main.util

import android.os.Build
import android.util.SparseIntArray
import androidx.annotation.DrawableRes
import androidx.core.util.set
import com.dede.android_eggs.R
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
        return when (apiLevel) {
            Build.VERSION_CODES.BASE,
            Build.VERSION_CODES.BASE_1_1 -> R.drawable.ic_android_classic

            Build.VERSION_CODES.CUPCAKE -> R.drawable.ic_android_cupcake
            Build.VERSION_CODES.DONUT -> R.drawable.ic_android_donut
            Build.VERSION_CODES.ECLAIR,
            Build.VERSION_CODES.ECLAIR_0_1,
            Build.VERSION_CODES.ECLAIR_MR1 -> R.drawable.ic_android_eclair

            Build.VERSION_CODES.FROYO -> R.drawable.ic_android_froyo
            else -> {
                if (cache.indexOfKey(apiLevel) >= 0) {
                    return cache[apiLevel]
                }
                val easterEgg = findEasterEgg(apiLevel)
                    ?: throw IllegalArgumentException("Not found Android logo res, level: $apiLevel")
                cache[apiLevel] = easterEgg.iconRes
                return easterEgg.iconRes
            }
        }
    }

}
