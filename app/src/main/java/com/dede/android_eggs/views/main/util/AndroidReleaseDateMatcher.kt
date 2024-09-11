package com.dede.android_eggs.views.main.util

import com.dede.basic.globalContext
import com.dede.basic.provider.TimelineEvent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

object AndroidReleaseDateMatcher {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface TimelineEventListEntryPoint {
        fun timelineEventList(): List<@JvmSuppressWildcards TimelineEvent>
    }

    private val timelines: List<TimelineEvent>

    init {
        val entryPoint = EntryPointAccessors
            .fromApplication(globalContext, TimelineEventListEntryPoint::class.java)
        timelines = entryPoint.timelineEventList()
    }

    fun findReleaseDateByApiLevel(apiLevel: Int): Date {
        val event = timelines.findLast { it.apiLevel == apiLevel }
            ?: throw IllegalArgumentException("")
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(event.year, event.month, 1)
        return calendar.time
    }
}
