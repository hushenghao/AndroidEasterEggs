package com.dede.android_eggs.views.main.util

import android.os.Build
import androidx.annotation.IntRange
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

    fun findReleaseDateByApiLevel(@IntRange(from = Build.VERSION_CODES.BASE.toLong()) apiLevel: Int): Date {
        val event = timelines.findLast { it.apiLevel == apiLevel }
            ?: throw IllegalArgumentException("Api level %d release date not fount!".format(apiLevel))
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(event.year, event.month, 1)
        return calendar.time
    }
}
