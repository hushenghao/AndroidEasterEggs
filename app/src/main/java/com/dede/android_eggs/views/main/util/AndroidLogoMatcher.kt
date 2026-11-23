package com.dede.android_eggs.views.main.util

import androidx.annotation.DrawableRes
import com.dede.basic.provider.AndroidVersion
import com.dede.basic.provider.EasterEgg
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidLogoMatcher @Inject constructor(
    private val easterEggs: List<@JvmSuppressWildcards EasterEgg>,
) {

    private val cache = HashMap<AndroidVersion, Int>()

    fun findEasterEggByVersion(version: AndroidVersion): EasterEgg? {
        return easterEggs.find { version in it.versions }
    }

    @DrawableRes
    fun findAndroidLogoByVersion(version: AndroidVersion): Int {
        cache[version]?.let { return it }
        val easterEgg = findEasterEggByVersion(version)
            ?: throw IllegalArgumentException("Not found Android logo res, version: $version")
        cache[version] = easterEgg.iconRes
        return easterEgg.iconRes
    }

}