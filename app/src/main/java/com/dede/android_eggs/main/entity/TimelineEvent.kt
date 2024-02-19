package com.dede.android_eggs.main.entity

import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.dede.android_eggs.main.EasterEggHelp
import java.util.Calendar
import java.util.TimeZone

data class TimelineEvent(
    val year: String?,
    val month: String?,
    val apiLevel: Int,
    val event: CharSequence,
) {

    companion object {

        private fun timelineEvent(apiLevel: Int, event: CharSequence): TimelineEvent {
            val regex =
                Regex("(January|February|March|April|May|June|July|August|September|October|November|December) +(\\d{4,})")
            val result = regex.find(event)
            var year: String? = null
            var month: String? = null
            if (result != null) {
                month = result.groups[1]?.value
                year = result.groups[2]?.value
            }
            return TimelineEvent(year, month, apiLevel, event)
        }

        val timelines = listOf(
            TimelineEvent(
                "2024", "September",
                35,
                "Vanilla Ice Cream."
            ),
            TimelineEvent(
                "2023", "September",
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
                "Upside Down Cake."
            ),
            TimelineEvent(
                "2022", "September",
                Build.VERSION_CODES.TIRAMISU,
                "Tiramisu."
            ),
            TimelineEvent(
                "2021", "December",
                Build.VERSION_CODES.S_V2,
                "S V2.\nOnce more unto the breach, dear friends, once more."
            ),
            TimelineEvent(
                "2021", "September",
                Build.VERSION_CODES.S,
                "S."
            ),
            timelineEvent(
                Build.VERSION_CODES.R,
                "R.\nReleased publicly as Android 11 in September 2020."
            ),
            timelineEvent(
                Build.VERSION_CODES.Q,
                "Q.\nReleased publicly as Android 10 in September 2019."
            ),
            timelineEvent(
                Build.VERSION_CODES.P,
                "P.\nReleased publicly as Android 9 in August 2018."
            ),
            timelineEvent(
                Build.VERSION_CODES.O_MR1,
                "O MR1.\nReleased publicly as Android 8.1 in December 2017."
            ),
            timelineEvent(
                Build.VERSION_CODES.O,
                "O.\nReleased publicly as Android 8.0 in August 2017."
            ),
            timelineEvent(
                Build.VERSION_CODES.N_MR1,
                "N MR1.\nReleased publicly as Android 7.1 in October 2016."
            ),
            timelineEvent(
                Build.VERSION_CODES.N,
                "N.\nReleased publicly as Android 7.0 in August 2016."
            ),
            timelineEvent(
                Build.VERSION_CODES.M,
                "M.\nReleased publicly as Android 6.0 in October 2015."
            ),
            timelineEvent(
                Build.VERSION_CODES.LOLLIPOP_MR1,
                "L MR1.\nReleased publicly as Android 5.1 in March 2015."
            ),
            timelineEvent(
                Build.VERSION_CODES.LOLLIPOP,
                "L.\nReleased publicly as Android 5.0 in November 2014."
            ),
            timelineEvent(
                Build.VERSION_CODES.KITKAT_WATCH,
                "K for watches.\nReleased publicly as Android 4.4W in June 2014."
            ),
            timelineEvent(
                Build.VERSION_CODES.KITKAT,
                "K.\nReleased publicly as Android 4.4 in October 2013."
            ),
            timelineEvent(
                Build.VERSION_CODES.JELLY_BEAN_MR2,
                "J MR2.\nReleased publicly as Android 4.3 in July 2013."
            ),
            timelineEvent(
                Build.VERSION_CODES.JELLY_BEAN_MR1,
                "J MR1.\nReleased publicly as Android 4.2 in November 2012."
            ),
            timelineEvent(
                Build.VERSION_CODES.JELLY_BEAN,
                "J.\nReleased publicly as Android 4.1 in July 2012."
            ),
            timelineEvent(
                Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1,
                "I MR1.\nReleased publicly as Android 4.03 in December 2011."
            ),
            timelineEvent(
                Build.VERSION_CODES.ICE_CREAM_SANDWICH,
                "I.\nReleased publicly as Android 4.0 in October 2011."
            ),
            timelineEvent(
                Build.VERSION_CODES.HONEYCOMB_MR2,
                "H MR2.\nReleased publicly as Android 3.2 in July 2011."
            ),
            timelineEvent(
                Build.VERSION_CODES.HONEYCOMB_MR1,
                "H MR1.\nReleased publicly as Android 3.1 in May 2011."
            ),
            timelineEvent(
                Build.VERSION_CODES.HONEYCOMB,
                "H.\nReleased publicly as Android 3.0 in February 2011."
            ),
            timelineEvent(
                Build.VERSION_CODES.GINGERBREAD_MR1,
                "G MR1.\nReleased publicly as Android 2.3.3 in February 2011."
            ),
            timelineEvent(
                Build.VERSION_CODES.GINGERBREAD,
                "G.\nReleased publicly as Android 2.3 in December 2010."
            ),
            timelineEvent(
                Build.VERSION_CODES.FROYO,
                "F.\nReleased publicly as Android 2.2 in May 2010."
            ),
            timelineEvent(
                Build.VERSION_CODES.ECLAIR_MR1,
                "E MR1.\nReleased publicly as Android 2.1 in January 2010."
            ),
            timelineEvent(
                Build.VERSION_CODES.ECLAIR_0_1,
                "E incremental update.\nReleased publicly as Android 2.0.1 in December 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.ECLAIR,
                "E.\nReleased publicly as Android 2.0 in October 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.DONUT,
                "D.\nReleased publicly as Android 1.6 in September 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.CUPCAKE,
                "C.\nReleased publicly as Android 1.5 in April 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.BASE_1_1,
                "First Android update.\nReleased publicly as Android 1.1 in February 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.BASE,
                "The original, first, version of Android. Yay!\nReleased publicly as Android 1.0 in September 2008."
            )
        )

        fun TimelineEvent.isNewGroup(): Boolean {
            val index = timelines.indexOf(this)
            if (index == -1) return true
            if (index == 0) return true
            val last = timelines[index - 1]
            return last.year != this.year
        }

        fun TimelineEvent.isLast(): Boolean {
            return timelines.last() === this
        }
    }

    val localYear: String?
        get() {
            val yearNum = year?.toIntOrNull() ?: return year
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.set(Calendar.YEAR, yearNum)
            return EasterEggHelp.DateFormatter.getInstance("yyyy").format(calendar.time)
        }

    val localMonth: String?
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

    val eventSpan: CharSequence by lazy(LazyThreadSafetyMode.NONE) {
        val split = event.split("\n")
        val span = SpannableStringBuilder()
        if (split.isNotEmpty()) {
            span.append(
                split[0],
                StyleSpan(Typeface.BOLD),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        if (split.size > 1) {
            span.appendLine()
            for (i in 1 until split.size) {
                span.append(split[i])
            }
        }
        span
    }
}
