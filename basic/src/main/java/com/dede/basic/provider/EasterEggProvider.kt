package com.dede.basic.provider

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

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

open class EasterEgg(
    @DrawableRes val iconRes: Int,
    @StringRes val nameRes: Int,
    @StringRes val nicknameRes: Int,
    override val apiLevelRange: IntRange,
    val actionClass: Class<out Activity>? = null,
) : BaseEasterEgg {

    constructor(
        @DrawableRes iconRes: Int,
        @StringRes nameRes: Int,
        @StringRes nicknameRes: Int,
        apiLevel: Int,
        actionClass: Class<out Activity>? = null,
    ) : this(iconRes, nameRes, nicknameRes, apiLevel..apiLevel, actionClass)

    val supportAdaptiveIcon: Boolean
        get() = apiLevelRange.first >= Build.VERSION_CODES.LOLLIPOP

    open fun onEasterEggAction(context: Context): Boolean {
        return false
    }

    open fun provideSnapshotProvider(): SnapshotProvider? {
        return null
    }

    final override fun hashCode(): Int {
        return apiLevelRange.hashCode()
    }

    final override fun equals(other: Any?): Boolean {
        if (other !is EasterEgg) {
            return false
        }
        return apiLevelRange == other.apiLevelRange
    }

}
