package com.dede.basic.provider

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.util.Calendar
import java.util.Date

interface EasterEggProvider {
    fun provideEasterEgg(): BaseEasterEgg

    fun provideTimelineEvents(): List<TimelineEvent>
}

interface BaseEasterEgg {
    fun getSortValue(): Int
}

data class TimelineEvent(
    /**
     * Event year
     * @see Calendar.YEAR
     */
    val year: Int,
    /**
     * Event month
     * @see Calendar.MONTH
     */
    val month: Int,
    val apiLevel: Int,
    val event: CharSequence,
) {

    companion object {

        @JvmStatic
        fun timelineEvent(apiLevel: Int, event: CharSequence): TimelineEvent {
            val regex =
                Regex("(January|February|March|April|May|June|July|August|September|October|November|December) +(\\d{4,})")
            val result = regex.find(event)
            if (result == null || result.groups.size < 2) {
                throw IllegalArgumentException("Event mismatch month and year, event: $event")
            }
            val yearStr = result.groups[2]!!.value
            val monthStr = result.groups[1]?.value
            val year: Int
            try {
                year = yearStr.toInt()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Illegal event year, year: $yearStr", e)
            }
            val month = when (monthStr) {
                "January" -> Calendar.JANUARY
                "February" -> Calendar.FEBRUARY
                "March" -> Calendar.MARCH
                "April" -> Calendar.APRIL
                "May" -> Calendar.MAY
                "June" -> Calendar.JUNE
                "July" -> Calendar.JULY
                "August" -> Calendar.AUGUST
                "September" -> Calendar.SEPTEMBER
                "October" -> Calendar.OCTOBER
                "November" -> Calendar.NOVEMBER
                "December" -> Calendar.DECEMBER
                else -> throw IllegalArgumentException("Illegal event month: $monthStr")
            }
            return TimelineEvent(year, month, apiLevel, event)
        }
    }
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

    open fun easterEggAction(context: Context): Boolean {
        return false
    }

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