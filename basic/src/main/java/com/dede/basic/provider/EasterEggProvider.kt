package com.dede.basic.provider

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.util.Date

interface EasterEggProvider {
    fun provideEasterEgg(): BaseEasterEgg
}

interface BaseEasterEgg {
    fun getSortValue(): Int
}

class EasterEggGroup(vararg val eggs: EasterEgg) : BaseEasterEgg {

    private val apiLevel = eggs.first().apiLevel.first..eggs.last().apiLevel.last

    override fun getSortValue(): Int {
        return apiLevel.first
    }

    override fun hashCode(): Int {
        return apiLevel.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EasterEggGroup) {
            return false
        }
        return apiLevel == other.apiLevel
    }
}

abstract class EasterEgg constructor(
    @DrawableRes val iconRes: Int,
    @StringRes val nameRes: Int,
    @StringRes val nicknameRes: Int,
    val apiLevel: IntRange,
    val supportAdaptiveIcon: Boolean = true,
) : BaseEasterEgg {

    constructor(
        @DrawableRes iconRes: Int,
        @StringRes nameRes: Int,
        @StringRes nicknameRes: Int,
        apiLevel: Int,
        supportAdaptiveIcon: Boolean = true,
    ) : this(iconRes, nameRes, nicknameRes, apiLevel..apiLevel, supportAdaptiveIcon)

    val id = apiLevel.first

    abstract fun provideEasterEgg(): Class<out Activity>?

    abstract fun provideSnapshotProvider(): SnapshotProvider?

    open fun getReleaseDate(): Date = Date()

    override fun getSortValue(): Int {
        return apiLevel.first
    }

    override fun hashCode(): Int {
        return apiLevel.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EasterEgg) {
            return false
        }
        return apiLevel == other.apiLevel
    }

}