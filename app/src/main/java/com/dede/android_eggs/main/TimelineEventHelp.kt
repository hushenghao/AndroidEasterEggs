package com.dede.android_eggs.main

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.dede.basic.provider.TimelineEvent
import java.util.Calendar
import java.util.TimeZone

object TimelineEventHelp {

    fun TimelineEvent.isNewGroup(timelines: List<TimelineEvent>): Boolean {
        val index = timelines.indexOf(this)
        if (index == -1) return true
        if (index == 0) return true
        val last = timelines[index - 1]
        return last.year != this.year
    }

    val TimelineEvent.localYear: String?
        get() {
            val yearNum = year?.toIntOrNull() ?: return year
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.set(Calendar.YEAR, yearNum)
            return EasterEggHelp.DateFormatter.getInstance("yyyy").format(calendar.time)
        }

    val TimelineEvent.localMonth: String?
        get() {
            val m = when (month) {
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
                else -> return month
            }
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.set(Calendar.MONTH, m)
            return EasterEggHelp.DateFormatter.getInstance("MMMM").format(calendar.time)
        }

    val TimelineEvent.eventAnnotatedString: AnnotatedString
        get() {
            return buildAnnotatedString {
                val split = event.split("\n")
                if (split.isNotEmpty()) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(split[0])
                    }
                }
                if (split.size > 1) {
                    appendLine()
                    for (i in 1 until split.size) {
                        append(split[i])
                    }
                }
            }
        }
}