package com.dede.android_eggs.views.main.util

import com.dede.basic.provider.AndroidVersion
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

    fun findReleaseDateByVersion(version: AndroidVersion): Date? {
        val event = timelines.findLast { it.version == version } ?: return null
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(event.year, event.month, 1)
        return calendar.time
    }

}