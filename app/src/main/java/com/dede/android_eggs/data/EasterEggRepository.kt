package com.dede.android_eggs.data

import androidx.annotation.DrawableRes
import com.dede.android_eggs.settings_ui.widgets.WidgetPreviewProvider
import com.dede.basic.provider.AndroidVersion
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.ComponentProvider
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.TimelineEvent
import com.dede.basic.provider.findEggByPublicApiLevel
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single entry point over every Easter egg registry list, in place of the scattered
 * `List<BaseEasterEgg>` / `List<EasterEgg>` / `List<TimelineEvent>` / `List<Component>`
 * injections consumed by the view layer.
 */
@Singleton
class EasterEggRepository @Inject constructor(
    eggs: List<@JvmSuppressWildcards BaseEasterEgg>,
    pureEggs: List<@JvmSuppressWildcards EasterEgg>,
    timelines: List<@JvmSuppressWildcards TimelineEvent>,
    components: List<@JvmSuppressWildcards ComponentProvider.Component>,
    widgetSet: Set<@JvmSuppressWildcards WidgetPreviewProvider>,
) {

    /** Grouped egg list (keeps [com.dede.basic.provider.EasterEggGroup] entries). */
    val easterEggs: List<BaseEasterEgg> = eggs

    /** Flattened egg list, one entry per released egg. */
    val pureEasterEggs: List<EasterEgg> = pureEggs

    val timelines: List<TimelineEvent> = timelines

    val components: List<ComponentProvider.Component> = components

    val widgets: List<WidgetPreviewProvider> = widgetSet.sortedBy { it.order }

    private val logoCache = HashMap<AndroidVersion, Int>()

    fun findEggByPublicApiLevel(apiLevel: Int): EasterEgg? =
        pureEasterEggs.findEggByPublicApiLevel(apiLevel)

    private fun findEasterEggByVersion(version: AndroidVersion): EasterEgg? =
        pureEasterEggs.find { version in it.versions }

    @DrawableRes
    fun findAndroidLogoByVersion(version: AndroidVersion): Int {
        logoCache[version]?.let { return it }
        val easterEgg = findEasterEggByVersion(version)
            ?: throw IllegalArgumentException("Not found Android logo res, version: $version")
        logoCache[version] = easterEgg.iconRes
        return easterEgg.iconRes
    }

    fun findReleaseDateByVersion(version: AndroidVersion): Date? {
        val event = timelines.findLast { it.version == version } ?: return null
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(event.year, event.month, 1)
        return calendar.time
    }
}