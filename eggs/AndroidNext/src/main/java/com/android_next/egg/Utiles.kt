package com.android_next.egg

import android.content.Context
import com.dede.basic.utils.AppLocaleDateFormatter
import java.util.Calendar

internal const val MONTH_CYCLE = 7
internal val MONTHS = intArrayOf(
    Calendar.JANUARY,
    Calendar.FEBRUARY,
    Calendar.MARCH,
    Calendar.APRIL,
    Calendar.MAY,
    Calendar.JUNE,
    Calendar.JULY,
    Calendar.AUGUST,
    Calendar.SEPTEMBER,
    Calendar.OCTOBER,
    Calendar.NOVEMBER,
    Calendar.DECEMBER,
)

internal fun Calendar.setDateZero(): Calendar {
    clear(Calendar.HOUR_OF_DAY)
    clear(Calendar.MINUTE)
    clear(Calendar.SECOND)
    clear(Calendar.MILLISECOND)
    return this
}

internal fun getReleaseCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(AndroidNextEasterEgg.RELEASE_YEAR, AndroidNextEasterEgg.RELEASE_MONTH, 1)
    calendar.setDateZero()
    return calendar
}

internal fun getReleaseCycleMonths(context: Context): List<String> {
    val calendar = getReleaseCalendar()
    val list = ArrayList<String>()
    // add final release label
    list.add(context.getString(R.string.label_timeline_final_release))
    val format = AppLocaleDateFormatter.getInstance("MMM")
    var c = 1
    do {
        calendar.add(Calendar.MONTH, -1)
        list.add(format.format(calendar.time))
        c++
    } while (c < MONTH_CYCLE)
    list.reverse()
    return list
}

internal fun getDateDiffMonth(start: Calendar, end: Calendar): Int {
    val yearDiff = end[Calendar.YEAR] - start[Calendar.YEAR]
    val monthDiff = end[Calendar.MONTH] - start[Calendar.MONTH]
    return yearDiff * 12 + monthDiff
}
