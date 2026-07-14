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

    val fullApiLevelRange: IntRange

    val fullApiLevel: Int
        get() = fullApiLevelRange.first
}

class EasterEggGroup(vararg val eggs: EasterEgg) : BaseEasterEgg {

    private val _fixedFullApiLevelRange: IntRange = 
        eggs.minOf { it.fullApiLevelRange.first }..eggs.maxOf { it.fullApiLevelRange.last }

    var selectedIndex: Int = 0

    override val fullApiLevelRange: IntRange = _fixedFullApiLevelRange

    override fun hashCode(): Int {
        return fullApiLevelRange.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EasterEggGroup) {
            return false
        }
        return fullApiLevelRange == other.fullApiLevelRange
    }
}

fun Int.toRange(): IntRange {
    return this..this
}

fun IntRange.toApiLevelRange(): IntRange {
    return with(EasterEgg.VERSION_CODES_FULL) {
        first.toApiLevel()..last.toApiLevel()
    }
}

open class EasterEgg @JvmOverloads constructor(
    @DrawableRes val iconRes: Int,
    @StringRes val nameRes: Int,
    @StringRes val nicknameRes: Int,
    override val fullApiLevelRange: IntRange,
    val actionClass: Class<out Activity>? = null,
) : BaseEasterEgg {

    @Suppress("ClassName")
    object VERSION_CODES_FULL {

        fun Int.toFullApiLevel(): Int {
            return if (isFullApiLevel()) this else this * SDK_INT_MULTIPLIER
        }

        fun Int.toApiLevel(): Int {
            return if (!isFullApiLevel()) {
                this
            } else {
                val majorSdkVersion = Build.getMajorSdkVersion(this)
                val minorSdkVersion = Build.getMinorSdkVersion(this)
                majorSdkVersion + if (minorSdkVersion == SDK_INT_MULTIPLIER - 1) 1 else 0
            }
        }

        fun Int.isFullApiLevel(): Boolean {
            return this >= SDK_INT_MULTIPLIER
        }

        // android.os.Build.VERSION_CODES_FULL#SDK_INT_MULTIPLIER
        const val SDK_INT_MULTIPLIER = 100000

        const val T_BETA = Build.VERSION_CODES_FULL.TIRAMISU - 1
        const val N_PREVIEW = Build.VERSION_CODES_FULL.N - 1
        const val M_PREVIEW = Build.VERSION_CODES_FULL.M - 1
        const val L_PREVIEW = Build.VERSION_CODES_FULL.LOLLIPOP - 1
        const val K_PREVIEW = Build.VERSION_CODES_FULL.KITKAT - 1
        const val ICS_PREVIEW = Build.VERSION_CODES_FULL.ICE_CREAM_SANDWICH - 1
    }

    @Suppress("ClassName")
    object VERSION_CODES {
    }

    constructor(
        @DrawableRes iconRes: Int,
        @StringRes nameRes: Int,
        @StringRes nicknameRes: Int,
        fullApiLevel: Int,
        actionClass: Class<out Activity>? = null,
    ) : this(iconRes, nameRes, nicknameRes, fullApiLevel.toRange(), actionClass)

    open fun onEasterEggAction(context: Context): Boolean {
        return false
    }

    open fun provideSnapshotProvider(): SnapshotProvider? {
        return null
    }

    final override fun hashCode(): Int {
        return fullApiLevelRange.hashCode()
    }

    final override fun equals(other: Any?): Boolean {
        if (other !is EasterEgg) {
            return false
        }
        return fullApiLevelRange == other.fullApiLevelRange
    }

}
