package com.dede.android_eggs.views.timeline

import android.os.Build
import android.util.SparseIntArray
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.util.set
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EasterEggHelp
import com.dede.basic.provider.EasterEgg
import javax.inject.Inject

class AndroidLogoMatcher @Inject constructor() {

    private val cache = SparseIntArray()

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards EasterEgg>

    @Composable
    fun findAndroidLogoComposable(apiLevel: Int): Int {
        if (LocalInspectionMode.current && !::easterEggs.isInitialized) {
            easterEggs = EasterEggHelp.previewEasterEggs()
        }
        return findAndroidLogo(apiLevel)
    }

    fun findEasterEgg(apiLevel: Int): EasterEgg? {
        return easterEggs.find { apiLevel in it.apiLevel }
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
                val easterEgg = easterEggs.find { apiLevel in it.apiLevel }
                    ?: throw IllegalArgumentException("Not found Android logo res, level: $apiLevel")
                cache[apiLevel] = easterEgg.iconRes
                return easterEgg.iconRes
            }
        }
    }

}