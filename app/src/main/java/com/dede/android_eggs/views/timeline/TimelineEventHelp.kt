package com.dede.android_eggs.views.timeline

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.basic.provider.TimelineEvent
import java.util.Calendar
import java.util.TimeZone

object TimelineEventHelp {

    object EventComparator : Comparator<TimelineEvent> {
        override fun compare(o1: TimelineEvent, o2: TimelineEvent): Int {
            var order = o2.year.compareTo(o1.year)
            if (order == 0) {
                order = o2.month.compareTo(o1.month)
            }
            if (order == 0) {
                order = o2.apiLevel.compareTo(o1.apiLevel)
            }
            return order
        }
    }

    fun TimelineEvent.isNewGroup(timelines: List<TimelineEvent>): Boolean {
        val index = timelines.indexOf(this)
        if (index == -1) return true
        if (index == 0) return true
        val last = timelines[index - 1]
        return last.year != this.year
    }

    val TimelineEvent.localYear: String
        get() {
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.set(Calendar.YEAR, year)
            return EasterEggHelp.DateFormatter.getInstance("yyyy").format(calendar.time)
        }

    val TimelineEvent.localMonth: String
        get() {
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.set(Calendar.MONTH, month)
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