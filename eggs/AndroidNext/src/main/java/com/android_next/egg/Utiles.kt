package com.android_next.egg

import java.util.Calendar

internal fun Calendar.setDateZero(): Calendar {
    clear(Calendar.HOUR_OF_DAY)
    clear(Calendar.MINUTE)
    clear(Calendar.SECOND)
    clear(Calendar.MILLISECOND)
    return this
}

internal fun getReleaseDate(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(AndroidNextEasterEgg.RELEASE_YEAR, AndroidNextEasterEgg.RELEASE_MONTH, 1)
    calendar.setDateZero()
    return calendar
}
