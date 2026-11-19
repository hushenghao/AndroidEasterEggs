package com.dede.android_eggs.baselineprofile

import android.content.Intent
import android.net.Uri

const val PACKAGE_NAME = "com.dede.android_eggs"

class Egg(val apiLevel: Int, val activity: String)

/**
 * The Activity every `egg://easter_egg/api/<level>` deep link opens, as `package/Activity`.
 * Written by hand because this module only sees the app from the outside. The smoke test fails
 * when a level opens something else, so a new or renamed egg shows up there first.
 */
val EGGS: List<Egg> = listOf(
    1..1 to "com.android_b.egg.PlatLogoActivity\$Base",
    2..2 to "com.android_b.egg.PlatLogoActivity\$PetitFour",
    3..3 to "com.android_b.egg.PlatLogoActivity\$Cupcake",
    4..4 to "com.android_b.egg.PlatLogoActivity\$Donut",
    5..7 to "com.android_b.egg.PlatLogoActivity\$Eclair",
    8..8 to "com.android_b.egg.PlatLogoActivity\$Froyo",
    9..10 to "com.android_g.egg.PlatLogoActivity",
    11..13 to "com.android_h.egg.PlatLogoActivity",
    14..15 to "com.android_i.egg.PlatLogoActivity",
    16..18 to "com.android_j.egg.PlatLogoActivity",
    19..20 to "com.android_k.egg.PlatLogoActivity",
    21..22 to "com.android_l.egg.PlatLogoActivity",
    23..23 to "com.android_m.egg.PlatLogoActivity",
    24..25 to "com.android_n.egg.PlatLogoActivity",
    26..26 to "com.android_o.egg.PlatLogoActivity",
    // 8.1 has an entry point of its own
    27..27 to "com.android_o.egg.PlatLogoActivity\$Point1",
    28..28 to "com.android_p.egg.PlatLogoActivity",
    29..29 to "com.android_q.egg.PlatLogoActivity",
    30..30 to "com.android_r.egg.PlatLogoActivity",
    31..32 to "com.android_s.egg.PlatLogoActivity",
    33..33 to "com.android_t.egg.PlatLogoActivity",
    34..34 to "com.android_u.egg.PlatLogoActivity",
    35..35 to "com.android_v.egg.PlatLogoActivity",
    36..36 to "com.android_baklava.egg.PlatLogoActivity",
    37..37 to "com.android_cinnamon_bun.egg.PlatLogoActivity",
).flatMap { (levels, activity) ->
    levels.map { Egg(it, "$PACKAGE_NAME/$activity") }
}

fun eggDeepLink(apiLevel: Int): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse("egg://easter_egg/api/$apiLevel"))
        .setPackage(PACKAGE_NAME)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
