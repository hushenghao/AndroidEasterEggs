package com.dede.basic.provider

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dede.basic.provider.EasterEgg.VERSION_CODES_FULL.times

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

fun Int.toRange(): IntRange {
    return this..this
}

open class EasterEgg @JvmOverloads constructor(
    @DrawableRes val iconRes: Int,
    @StringRes val nameRes: Int,
    @StringRes val nicknameRes: Int,
    override val apiLevelRange: IntRange,
    val actionClass: Class<out Activity>? = null,
    val fullApiLevelRange: IntRange = apiLevelRange * VERSION_CODES_FULL.SDK_INT_MULTIPLIER,
) : BaseEasterEgg {

    @Suppress("ClassName")
    object VERSION_CODES_FULL {

        internal operator fun IntRange.times(multiplier: Int): IntRange {
            return (this.first * multiplier)..(this.last * multiplier)
        }

        fun Int.toFullApiLevel(): Int {
            return this * SDK_INT_MULTIPLIER
        }

        // android.os.Build.VERSION_CODES_FULL#SDK_INT_MULTIPLIER
        const val SDK_INT_MULTIPLIER = 100000

        const val L_PREVIEW = Build.VERSION_CODES.LOLLIPOP * SDK_INT_MULTIPLIER - 1
        const val BAKLAVA_1 = Build.VERSION_CODES.BAKLAVA * SDK_INT_MULTIPLIER + 1
    }

    @Suppress("ClassName")
    object VERSION_CODES {
        const val CINNAMON_BUN = 37// android 17
    }

    constructor(
        @DrawableRes iconRes: Int,
        @StringRes nameRes: Int,
        @StringRes nicknameRes: Int,
        apiLevel: Int,
        actionClass: Class<out Activity>? = null,
    ) : this(iconRes, nameRes, nicknameRes, apiLevel.toRange(), actionClass)

    open fun onEasterEggAction(context: Context): Boolean {
        return false
    }

    open fun provideSnapshotProvider(): SnapshotProvider? {
        return null
    }

    final override fun hashCode(): Int {
        return apiLevelRange.hashCode() * 31 + fullApiLevelRange.hashCode()
    }

    final override fun equals(other: Any?): Boolean {
        if (other !is EasterEgg) {
            return false
        }
        return apiLevelRange == other.apiLevelRange && apiLevelRange == other.apiLevelRange
    }

}
