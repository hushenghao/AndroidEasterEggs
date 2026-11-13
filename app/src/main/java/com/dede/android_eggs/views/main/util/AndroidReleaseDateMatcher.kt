package com.dede.android_eggs.views.main.util

import com.dede.basic.provider.TimelineEvent
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidReleaseDateMatcher @Inject constructor(
    private val timelines: List<@JvmSuppressWildcards TimelineEvent>,
) {

    fun findReleaseDateByFullApiLevel(fullApiLevel: Int): Date? {
        val event = timelines.findLast { it.fullApiLevel == fullApiLevel } ?: return null
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(event.year, event.month, 1)
        return calendar.time
    }

    fun requireReleaseDateByFullApiLevel(fullApiLevel: Int): Date {
        val event = timelines.findLast { it.fullApiLevel == fullApiLevel }
            ?: throw IllegalArgumentException(
                "Full api level %d release date not found!".format(fullApiLevel)
            )
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(event.year, event.month, 1)
        return calendar.time
    }

}
