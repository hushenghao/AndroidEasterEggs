package com.dede.android_eggs.views.main

import com.android_g.egg.AndroidGingerbreadEasterEgg
import com.android_h.egg.AndroidHoneycombEasterEgg
import com.android_i.egg.AndroidIceCreamSandwichEasterEgg
import com.android_j.egg.AndroidJellyBeanEasterEgg
import com.android_k.egg.AndroidKitKatEasterEgg
import com.android_l.egg.AndroidLollipopEasterEgg
import com.android_m.egg.AndroidMarshmallowEasterEgg
import com.android_n.egg.AndroidNougatEasterEgg
import com.android_o.egg.AndroidOreoEasterEgg
import com.android_p.egg.AndroidPieEasterEgg
import com.android_q.egg.AndroidQEasterEgg
import com.android_r.egg.AndroidREasterEgg
import com.android_s.egg.AndroidSEasterEgg
import com.android_t.egg.AndroidTEasterEgg
import com.android_u.egg.AndroidUEasterEgg
import com.dede.android_eggs.main.entity.Snapshot
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.ComponentProvider.Component
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import com.dede.basic.provider.SnapshotProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(
    includes = [
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
    ]
)
@InstallIn(SingletonComponent::class)
object EasterEggModules {

    @Provides
    @Singleton
    fun provideEasterEggList(easterEggSet: Set<@JvmSuppressWildcards BaseEasterEgg>): List<@JvmSuppressWildcards BaseEasterEgg> {
        return easterEggSet.sortedByDescending { it.getSortValue() }
    }

    @Provides
    @Singleton
    fun providePureEasterEggList(easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>): List<@JvmSuppressWildcards EasterEgg> {
        val list = ArrayList<EasterEgg>()
        for (easterEgg in easterEggs) {
            if (easterEgg is EasterEggGroup) {
                list.addAll(easterEgg.eggs)
            } else if (easterEgg is EasterEgg) {
                list.add(easterEgg)
            }
        }
        return list
    }

    @Provides
    @Singleton
    fun provideSnapshotList(easterEggs: List<@JvmSuppressWildcards EasterEgg>): List<@JvmSuppressWildcards Snapshot> {
        val list = ArrayList<Snapshot>()
        var provider: SnapshotProvider?
        for (easterEgg in easterEggs) {
            provider = easterEgg.provideSnapshotProvider()
            if (provider != null) {
                list.add(Snapshot(provider, easterEgg.id))
            }
        }
        return list
    }

    @Provides
    @Singleton
    fun provideEasterEggAdaptiveIconRes(easterEggs: List<@JvmSuppressWildcards EasterEgg>): IntArray {
        return easterEggs.filter { it.supportAdaptiveIcon }
            .map { it.iconRes }.toIntArray()
    }

    @Provides
    @Singleton
    fun provideEasterEggComponents(componentSet: Set<@JvmSuppressWildcards Component>): List<@JvmSuppressWildcards Component> {
        return componentSet.sortedByDescending { it.getSortValue() }
    }

}
