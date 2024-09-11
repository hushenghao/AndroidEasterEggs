package com.dede.basic.provider

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.util.Date

interface EasterEggProvider {
    fun provideEasterEgg(): BaseEasterEgg

    fun provideTimelineEvents(): List<TimelineEvent>
}

interface BaseEasterEgg {

    val apiLevelRange: IntRange

    val apiLevel: Int
        get() = apiLevelRange.first
}

class EasterEggGroup(vararg val eggs: EasterEgg) : BaseEasterEgg {

    override val apiLevelRange: IntRange
        get() = eggs.first().apiLevelRange.first..eggs.last().apiLevelRange.last

    override fun hashCode(): Int {
        return apiLevelRange.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EasterEggGroup) {
            return false
        }
        return apiLevelRange == other.apiLevelRange
    }
}

abstract class EasterEgg(
    @DrawableRes val iconRes: Int,
    @StringRes val nameRes: Int,
    @StringRes val nicknameRes: Int,
    override val apiLevelRange: IntRange,
    val supportAdaptiveIcon: Boolean = true,
) : BaseEasterEgg {

    constructor(
        @DrawableRes iconRes: Int,
        @StringRes nameRes: Int,
        @StringRes nicknameRes: Int,
        apiLevel: Int,
        supportAdaptiveIcon: Boolean = true,
    ) : this(iconRes, nameRes, nicknameRes, apiLevel..apiLevel, supportAdaptiveIcon)

    abstract fun provideEasterEgg(): Class<out Activity>?

    open fun onEasterEggAction(context: Context): Boolean {
        return false
    }

    abstract fun provideSnapshotProvider(): SnapshotProvider?

    override fun hashCode(): Int {
        return apiLevelRange.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EasterEgg) {
            return false
        }
        return apiLevelRange == other.apiLevelRange
    }

}
