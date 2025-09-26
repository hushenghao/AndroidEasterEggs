package com.dede.basic.provider

import java.util.Calendar

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
    val fullApiLevel: Int = apiLevel * SDK_INT_MULTIPLIER,
) {

    companion object {

        // android.os.Build.VERSION_CODES_FULL#SDK_INT_MULTIPLIER
        const val SDK_INT_MULTIPLIER = 100000

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
