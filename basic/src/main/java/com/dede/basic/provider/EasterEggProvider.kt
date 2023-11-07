package com.dede.basic.provider

import android.app.Activity
import android.os.Build
import android.util.SparseArray
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface EasterEggProvider {
    fun provideEasterEgg(): BaseEasterEgg
}

interface BaseEasterEgg {
    fun getSortValue(): Int
}

class EasterEggGroup(vararg eggs: EasterEgg) : BaseEasterEgg {

    val eggs: Array<out EasterEgg> = eggs

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

    companion object {

        private val apiLevelArrays = SparseArray<String>()

        fun getVersionNameByApiLevel(level: Int): String {
            return apiLevelArrays[level]
                ?: throw IllegalArgumentException("Illegal Api level: $level")
        }

        init {
            apiLevelArrays[Build.VERSION_CODES.UPSIDE_DOWN_CAKE] = "14"
            apiLevelArrays[Build.VERSION_CODES.TIRAMISU] = "13"
            apiLevelArrays[Build.VERSION_CODES.S_V2] = "12L"
            apiLevelArrays[Build.VERSION_CODES.S] = "12"
            apiLevelArrays[Build.VERSION_CODES.R] = "11"
            apiLevelArrays[Build.VERSION_CODES.Q] = "10"
            apiLevelArrays[Build.VERSION_CODES.P] = "9"
            apiLevelArrays[Build.VERSION_CODES.O_MR1] = "8.1"
            apiLevelArrays[Build.VERSION_CODES.O] = "8.0"
            apiLevelArrays[Build.VERSION_CODES.N_MR1] = "7.1"
            apiLevelArrays[Build.VERSION_CODES.N] = "7.0"
            apiLevelArrays[Build.VERSION_CODES.M] = "6.0"
            apiLevelArrays[Build.VERSION_CODES.LOLLIPOP_MR1] = "5.1"
            apiLevelArrays[Build.VERSION_CODES.LOLLIPOP] = "5.0"
            apiLevelArrays[Build.VERSION_CODES.KITKAT_WATCH] = "4.4W"
            apiLevelArrays[Build.VERSION_CODES.KITKAT] = "4.4"
            apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN_MR2] = "4.3"
            apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN_MR1] = "4.2"
            apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN] = "4.1"
            apiLevelArrays[Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1] = "4.0.3"
            apiLevelArrays[Build.VERSION_CODES.ICE_CREAM_SANDWICH] = "4.0"
            apiLevelArrays[Build.VERSION_CODES.HONEYCOMB_MR2] = "3.2"
            apiLevelArrays[Build.VERSION_CODES.HONEYCOMB_MR1] = "3.1"
            apiLevelArrays[Build.VERSION_CODES.HONEYCOMB] = "3.0"
            apiLevelArrays[Build.VERSION_CODES.GINGERBREAD_MR1] = "2.3.3"
            apiLevelArrays[Build.VERSION_CODES.GINGERBREAD] = "2.3"
            apiLevelArrays[Build.VERSION_CODES.FROYO] = "2.2"
            apiLevelArrays[Build.VERSION_CODES.ECLAIR_MR1] = "2.1"
            apiLevelArrays[Build.VERSION_CODES.ECLAIR] = "2.0"
            apiLevelArrays[Build.VERSION_CODES.DONUT] = "1.6"
            apiLevelArrays[Build.VERSION_CODES.CUPCAKE] = "1.5"
            apiLevelArrays[Build.VERSION_CODES.BASE_1_1] = "1.1"
            apiLevelArrays[Build.VERSION_CODES.BASE] = "1.0"
        }

    }
}