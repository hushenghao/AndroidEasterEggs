package com.dede.android_eggs.main.entity

import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.prefs.LanguagePref
import java.text.Format
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

data class TimelineEvent(
    val year: String?,
    val month: String?,
    @DrawableRes val logoRes: Int,
    private val event: CharSequence,
) {

    companion object {

        private fun getApplicationLocale(): Locale {
            val locales = AppCompatDelegate.getApplicationLocales()
            return if (locales.isEmpty) {
                Locale.getDefault()
            } else {
                locales.get(0) ?: Locale.getDefault()
            }
        }

        private fun timelineEvent(@DrawableRes logoRes: Int, event: CharSequence): TimelineEvent {
            val regex =
                Regex("(January|February|March|April|May|June|July|August|September|October|November|December) +(\\d{4,})")
            val result = regex.find(event)
            var year: String? = null
            var month: String? = null
            if (result != null) {
                month = result.groups[1]?.value
                year = result.groups[2]?.value
            }
            return TimelineEvent(year, month, logoRes, event)
        }

        val timelines = listOf(
            TimelineEvent(
                "2023", "September",
                com.android_u.egg.R.drawable.u_android14_patch_adaptive,
                "Upside Down Cake."
            ),
            TimelineEvent(
                "2022", "September",
                com.android_t.egg.R.drawable.t_android_logo,
                "Tiramisu."
            ),
            TimelineEvent(
                "2021", "December",
                com.android_s.egg.R.drawable.s_android_logo,
                "S V2.\nOnce more unto the breach, dear friends, once more."
            ),
            TimelineEvent(
                "2021", "September",
                com.android_s.egg.R.drawable.s_android_logo,
                "S."
            ),
            timelineEvent(
                com.android_r.egg.R.drawable.r_icon,
                "R.\nReleased publicly as Android 11 in September 2020."
            ),
            timelineEvent(
                com.android_q.egg.R.drawable.q_icon,
                "Q.\nReleased publicly as Android 10 in September 2019."
            ),
            timelineEvent(
                com.android_p.egg.R.drawable.p_icon,
                "P.\nReleased publicly as Android 9 in August 2018."
            ),
            timelineEvent(
                com.android_o.egg.R.drawable.o_android_logo,
                "O MR1.\nReleased publicly as Android 8.1 in December 2017."
            ),
            timelineEvent(
                com.android_o.egg.R.drawable.o_android_logo,
                "O.\nReleased publicly as Android 8.0 in August 2017."
            ),
            timelineEvent(
                com.android_n.egg.R.drawable.n_android_logo,
                "N MR1.\nReleased publicly as Android 7.1 in October 2016."
            ),
            timelineEvent(
                com.android_n.egg.R.drawable.n_android_logo,
                "N.\nReleased publicly as Android 7.0 in August 2016."
            ),
            timelineEvent(
                com.android_m.egg.R.drawable.m_android_logo,
                "M.\nReleased publicly as Android 6.0 in October 2015."
            ),
            timelineEvent(
                com.android_l.egg.R.drawable.l_android_logo,
                "L MR1.\nReleased publicly as Android 5.1 in March 2015."
            ),
            timelineEvent(
                com.android_l.egg.R.drawable.l_android_logo,
                "L.\nReleased publicly as Android 5.0 in November 2014."
            ),
            timelineEvent(
                com.android_k.egg.R.drawable.k_android_logo,
                "K for watches.\nReleased publicly as Android 4.4W in June 2014."
            ),
            timelineEvent(
                com.android_k.egg.R.drawable.k_android_logo,
                "K.\nReleased publicly as Android 4.4 in October 2013."
            ),
            timelineEvent(
                com.android_j.egg.R.drawable.j_android_logo,
                "J MR2.\nReleased publicly as Android 4.3 in July 2013."
            ),
            timelineEvent(
                com.android_j.egg.R.drawable.j_android_logo,
                "J MR1.\nReleased publicly as Android 4.2 in November 2012."
            ),
            timelineEvent(
                com.android_j.egg.R.drawable.j_android_logo,
                "J.\nReleased publicly as Android 4.1 in July 2012."
            ),
            timelineEvent(
                com.android_i.egg.R.drawable.i_platlogo,
                "I MR1.\nReleased publicly as Android 4.03 in December 2011."
            ),
            timelineEvent(
                com.android_i.egg.R.drawable.i_platlogo,
                "I.\nReleased publicly as Android 4.0 in October 2011."
            ),
            timelineEvent(
                com.android_h.egg.R.drawable.h_android_logo,
                "H MR2.\nReleased publicly as Android 3.2 in July 2011."
            ),
            timelineEvent(
                com.android_h.egg.R.drawable.h_android_logo,
                "H MR1.\nReleased publicly as Android 3.1 in May 2011."
            ),
            timelineEvent(
                com.android_h.egg.R.drawable.h_android_logo,
                "H.\nReleased publicly as Android 3.0 in February 2011."
            ),
            timelineEvent(
                com.android_g.egg.R.drawable.g_android_logo,
                "G MR1.\nReleased publicly as Android 2.3.3 in February 2011."
            ),
            timelineEvent(
                com.android_g.egg.R.drawable.g_android_logo,
                "G.\nReleased publicly as Android 2.3 in December 2010."
            ),
            timelineEvent(
                R.drawable.ic_android_froyo,
                "F.\nReleased publicly as Android 2.2 in May 2010."
            ),
            timelineEvent(
                R.drawable.ic_android_eclair,
                "E MR1.\nReleased publicly as Android 2.1 in January 2010."
            ),
            timelineEvent(
                R.drawable.ic_android_eclair,
                "E incremental update.\nReleased publicly as Android 2.0.1 in December 2009."
            ),
            timelineEvent(
                R.drawable.ic_android_eclair,
                "E.\nReleased publicly as Android 2.0 in October 2009."
            ),
            timelineEvent(
                R.drawable.ic_android_donut,
                "D.\nReleased publicly as Android 1.6 in September 2009."
            ),
            timelineEvent(
                R.drawable.ic_android_cupcake,
                "C.\nReleased publicly as Android 1.5 in April 2009."
            ),
            timelineEvent(
                R.drawable.ic_android_classic,
                "First Android update.\nReleased publicly as Android 1.1 in February 2009."
            ),
            timelineEvent(
                R.drawable.ic_android_classic,
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
            val locale = getApplicationLocale()
            val format: Format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat("yyyy", locale)
            } else {
                java.text.SimpleDateFormat("yyyy", locale)
            }
            return format.format(calendar.time)
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
            val locale = getApplicationLocale()
            val format: Format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat("MMMM", locale)
            } else {
                java.text.SimpleDateFormat("MMMM", locale)
            }
            return format.format(calendar.time)
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
