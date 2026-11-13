package com.dede.android_eggs.inject

import com.dede.android_eggs.views.timeline.TimelineEventHelp
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.ComponentProvider.Component
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import com.dede.basic.provider.TimelineEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(
    includes = [
        AndroidCinnamonBunEasterEgg::class,
        AndroidBaklavaEasterEgg::class,
        AndroidVEasterEgg::class,
        AndroidUEasterEgg::class,
        AndroidTEasterEgg::class,
        AndroidSEasterEgg::class,
        AndroidREasterEgg::class,
        AndroidQEasterEgg::class,
        AndroidPieEasterEgg::class,
        AndroidOreoEasterEgg::class,
        AndroidNougatEasterEgg::class,
        AndroidMarshmallowEasterEgg::class,
        AndroidLollipopEasterEgg::class,
        AndroidKitKatEasterEgg::class,
        AndroidJellyBeanEasterEgg::class,
        AndroidIceCreamSandwichEasterEgg::class,
        AndroidHoneycombEasterEgg::class,
        AndroidGingerbreadEasterEgg::class,
        AndroidBaseEasterEgg::class,
    ],
)
@InstallIn(SingletonComponent::class)
object EasterEggModules {

    @Provides
    @Singleton
    fun provideTimelineEventList(timelineGroupSet: Set<@JvmSuppressWildcards List<@JvmSuppressWildcards TimelineEvent>>): List<@JvmSuppressWildcards TimelineEvent> {
        return buildList {
            for (events in timelineGroupSet) {
                addAll(events)
            }
        }.sortedWith(TimelineEventHelp.EventComparator)
    }

    @Provides
    @Singleton
    fun provideEasterEggList(easterEggSet: Set<@JvmSuppressWildcards BaseEasterEgg>): List<@JvmSuppressWildcards BaseEasterEgg> {
        return easterEggSet.sortedByDescending { it.fullApiLevel }
    }

    @Provides
    @Singleton
    fun providePureEasterEggList(easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>): List<@JvmSuppressWildcards EasterEgg> {
        return buildList {
            for (easterEgg in easterEggs) {
                if (easterEgg is EasterEggGroup) {
                    addAll(easterEgg.eggs)
                } else if (easterEgg is EasterEgg) {
                    add(easterEgg)
                }
            }
        }.sortedByDescending { it.fullApiLevel }
    }

}

@Module(
    includes = [
        AndroidVEasterEgg::class,
        AndroidTEasterEgg::class,
        AndroidSEasterEgg::class,
        AndroidREasterEgg::class,
        AndroidNougatEasterEgg::class,
        AndroidKitKatEasterEgg::class,
        AndroidJellyBeanEasterEgg::class,
    ],
)
@InstallIn(SingletonComponent::class)
object EasterEggComponentModules {

    @Provides
    @Singleton
    fun provideEasterEggComponents(componentSet: Set<@JvmSuppressWildcards Component>): List<@JvmSuppressWildcards Component> {
        return componentSet
            .filter { it.isSupported() }
            .sortedByDescending { it.fullApiLevel }
    }

}
