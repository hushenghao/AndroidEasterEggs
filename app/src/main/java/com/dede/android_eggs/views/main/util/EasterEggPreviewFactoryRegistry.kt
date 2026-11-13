package com.dede.android_eggs.views.main.util

import com.android_b.egg.AndroidBaseEasterEgg
import com.android_baklava.egg.AndroidBaklavaEasterEgg
import com.android_cinnamon_bun.egg.AndroidCinnamonBunEasterEgg
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
import com.android_v.egg.AndroidVEasterEgg
import com.dede.basic.provider.BaseEasterEgg

/**
 * Explicit factories for eggs used by Compose previews. AndroidNext is an optional app dependency
 * and is intentionally not listed here.
 */
internal object EasterEggPreviewFactoryRegistry {

    private val factories: List<() -> BaseEasterEgg> = listOf(
        { AndroidCinnamonBunEasterEgg.provideEasterEgg() },
        { AndroidBaklavaEasterEgg.provideEasterEgg() },
        { AndroidVEasterEgg.provideEasterEgg() },
        { AndroidUEasterEgg.provideEasterEgg() },
        { AndroidTEasterEgg.provideEasterEgg() },
        { AndroidSEasterEgg.provideEasterEgg() },
        { AndroidREasterEgg.provideEasterEgg() },
        { AndroidQEasterEgg.provideEasterEgg() },
        { AndroidPieEasterEgg.provideEasterEgg() },
        { AndroidOreoEasterEgg.provideEasterEgg() },
        { AndroidNougatEasterEgg.provideEasterEgg() },
        { AndroidMarshmallowEasterEgg.provideEasterEgg() },
        { AndroidLollipopEasterEgg.provideEasterEgg() },
        { AndroidKitKatEasterEgg.provideEasterEgg() },
        { AndroidJellyBeanEasterEgg.provideEasterEgg() },
        { AndroidIceCreamSandwichEasterEgg.provideEasterEgg() },
        { AndroidHoneycombEasterEgg.provideEasterEgg() },
        { AndroidGingerbreadEasterEgg.provideEasterEgg() },
        { AndroidBaseEasterEgg.provideFroyoEgg() },
        { AndroidBaseEasterEgg.provideEclairEgg() },
        { AndroidBaseEasterEgg.provideDonutEgg() },
        { AndroidBaseEasterEgg.provideCupcakeEgg() },
        { AndroidBaseEasterEgg.providePetitFourEgg() },
        { AndroidBaseEasterEgg.provideBaseEgg() },
    )

    fun createBaseEasterEggs(): List<BaseEasterEgg> {
        return factories.map { it() }
    }
}
