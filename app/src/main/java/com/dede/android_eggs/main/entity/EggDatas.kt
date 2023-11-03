package com.dede.android_eggs.main.entity

import android.os.Build.VERSION_CODES
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg.VersionCommentFormatter
import com.dede.android_eggs.main.entity.Egg.VersionFormatter


object EggDatas {

    private const val KEY_EGG_U = "key_egg_u"
    private const val KEY_EGG_T = "key_egg_t"
    private const val KEY_EGG_S = "key_egg_s"
    private const val KEY_EGG_R = "key_egg_r"
    private const val KEY_EGG_Q = "key_egg_q"
    private const val KEY_EGG_P = "key_egg_p"
    private const val KEY_EGG_O_1 = "key_egg_o_1"
    private const val KEY_EGG_O = "key_egg_o"
    private const val KEY_EGG_N = "key_egg_n"
    private const val KEY_EGG_M = "key_egg_m"
    private const val KEY_EGG_L = "key_egg_l"
    private const val KEY_EGG_K = "key_egg_k"
    private const val KEY_EGG_J = "key_egg_j"
    private const val KEY_EGG_I = "key_egg_i"
    private const val KEY_EGG_H = "key_egg_h"
    private const val KEY_EGG_G = "key_egg_g"

    val snapshotList = listOf(
        Snapshot(com.android_u.egg.PlatLogoSnapshotProvider(), KEY_EGG_U),
        Snapshot(com.android_t.egg.PlatLogoSnapshotProvider(), KEY_EGG_T),
        Snapshot(com.android_s.egg.PlatLogoSnapshotProvider(), KEY_EGG_S),
        Snapshot(com.android_r.egg.PlatLogoSnapshotProvider(), KEY_EGG_R),
        Snapshot(com.android_q.egg.PlatLogoSnapshotProvider(), KEY_EGG_Q),
        Snapshot(com.android_p.egg.PlatLogoSnapshotProvider(), KEY_EGG_P),
        Snapshot(com.android_o.egg.PlatLogoSnapshotProvider.Point1(), KEY_EGG_O_1),
        Snapshot(com.android_o.egg.PlatLogoSnapshotProvider(), KEY_EGG_O),
        Snapshot(com.android_n.egg.PlatLogoSnapshotProvider(), KEY_EGG_N),
        Snapshot(com.android_m.egg.PlatLogoSnapshotProvider(), KEY_EGG_M),
        Snapshot(com.android_l.egg.PlatLogoSnapshotProvider(), KEY_EGG_L),
        Snapshot(com.android_k.egg.PlatLogoSnapshotProvider(), KEY_EGG_K),
        Snapshot(com.android_j.egg.PlatLogoSnapshotProvider(), KEY_EGG_J),
        Snapshot(com.android_i.egg.PlatLogoSnapshotProvider(), KEY_EGG_I),
        Snapshot(com.android_h.egg.PlatLogoSnapshotProvider(), KEY_EGG_H),
        Snapshot(com.android_g.egg.PlatLogoSnapshotProvider(), KEY_EGG_G),
    )

    fun getPureEggList(): List<Egg> {
        val list = ArrayList<Egg>()
        for (vType in eggList) {
            if (vType is Egg) {
                list.add(vType)
            } else if (vType is EggGroup) {
                list.addAll(vType.child)
            }
        }
        return list
    }

    val eggList = listOf(
        Wavy(R.drawable.ic_wavy_line),
        Egg(
            com.android_u.egg.R.drawable.u_android14_patch_adaptive,
            R.string.nickname_android_u,
            VersionFormatter(R.string.nickname_android_u, "14"),
            VersionCommentFormatter(VERSION_CODES.UPSIDE_DOWN_CAKE, "14"),
            com.android_u.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_U
        ),
        Egg(
            R.drawable.ic_android_tiramisu,
            com.android_t.egg.R.string.t_egg_name,
            VersionFormatter(R.string.nickname_android_t, "13"),
            VersionCommentFormatter(VERSION_CODES.TIRAMISU, "13"),
            com.android_t.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_T
        ),
        Egg(
            R.drawable.ic_android_s,
            com.android_s.egg.R.string.s_egg_name,
            VersionFormatter(R.string.nickname_android_s, "12", "12L"),
            VersionCommentFormatter(VERSION_CODES.S, VERSION_CODES.S_V2, "12", "12L"),
            com.android_s.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_S
        ),
        Egg(
            com.android_r.egg.R.drawable.r_icon,
            com.android_r.egg.R.string.r_egg_name,
            VersionFormatter(R.string.nickname_android_r, "11"),
            VersionCommentFormatter(VERSION_CODES.R, "11"),
            com.android_r.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_R
        ),
        Egg(
            com.android_q.egg.R.drawable.q_icon,
            com.android_q.egg.R.string.q_egg_name,
            VersionFormatter(R.string.nickname_android_q, "10"),
            VersionCommentFormatter(VERSION_CODES.Q, "10"),
            com.android_q.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_Q
        ),
        Egg(
            com.android_p.egg.R.drawable.p_icon,
            com.android_p.egg.R.string.p_app_name,
            VersionFormatter(R.string.nickname_android_p, "9"),
            VersionCommentFormatter(VERSION_CODES.P, "9"),
            com.android_p.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_P
        ),
        EggGroup(
            0,
            Egg(
                R.drawable.ic_android_oreo,
                com.android_o.egg.R.string.o_app_name,
                VersionFormatter(R.string.nickname_android_o, "8.1"),
                VersionCommentFormatter(VERSION_CODES.O_MR1, "8.1"),
                com.android_o.egg.PlatLogoActivity.Point1::class.java,
                true,
                KEY_EGG_O_1
            ),
            Egg(
                R.drawable.ic_android_oreo,
                com.android_o.egg.R.string.o_app_name,
                VersionFormatter(R.string.nickname_android_o, "8.0"),
                VersionCommentFormatter(VERSION_CODES.O, "8.0"),
                com.android_o.egg.PlatLogoActivity::class.java,
                true,
                KEY_EGG_O,
            )
        ),
        Egg(
            R.drawable.ic_android_nougat,
            com.android_n.egg.R.string.n_app_name,
            VersionFormatter(R.string.nickname_android_n, "7.0", "7.1"),
            VersionCommentFormatter(VERSION_CODES.N, VERSION_CODES.N_MR1, "7.0", "7.1"),
            com.android_n.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_N
        ),
        Egg(
            R.drawable.ic_android_marshmallow,
            com.android_m.egg.R.string.m_mland,
            VersionFormatter(R.string.nickname_android_m, "6.0"),
            VersionCommentFormatter(VERSION_CODES.M, "6.0"),
            com.android_m.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_M
        ),
        Egg(
            R.drawable.ic_android_lollipop,
            com.android_l.egg.R.string.l_lland,
            VersionFormatter(R.string.nickname_android_l, "5.0", "5.1"),
            VersionCommentFormatter(
                VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1,
                "5.0", "5.1"
            ),
            com.android_l.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_L
        ),
        Egg(
            R.drawable.ic_android_kitkat,
            com.android_k.egg.R.string.k_dessert_case,
            VersionFormatter(R.string.nickname_android_k, "4.4", "4.4W"),
            VersionCommentFormatter(
                VERSION_CODES.KITKAT, VERSION_CODES.KITKAT_WATCH,
                "4.4", "4.4W"
            ),
            com.android_k.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_K
        ),
        Egg(
            R.drawable.ic_android_jelly_bean,
            com.android_j.egg.R.string.j_egg_name,
            VersionFormatter(R.string.nickname_android_j, "4.1", "4.3"),
            VersionCommentFormatter(
                VERSION_CODES.JELLY_BEAN, VERSION_CODES.JELLY_BEAN_MR2,
                "4.1", "4.3"
            ),
            com.android_j.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_J
        ),
        Egg(
            R.drawable.ic_android_ics,
            com.android_i.egg.R.string.i_egg_name,
            VersionFormatter(R.string.nickname_android_i, "4.0", "4.0.3"),
            VersionCommentFormatter(
                VERSION_CODES.ICE_CREAM_SANDWICH, VERSION_CODES.ICE_CREAM_SANDWICH_MR1,
                "4.0", "4.0.3"
            ),
            com.android_i.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_I
        ),
        Egg(
            R.drawable.ic_android_honeycomb,
            com.android_h.egg.R.string.h_egg_name,
            VersionFormatter(R.string.nickname_android_h, "3.0", "3.2"),
            VersionCommentFormatter(
                VERSION_CODES.HONEYCOMB, VERSION_CODES.HONEYCOMB_MR2,
                "3.0", "3.2"
            ),
            com.android_h.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_H
        ),
        Egg(
            R.drawable.ic_android_gingerbread,
            com.android_g.egg.R.string.g_egg_name,
            VersionFormatter(R.string.nickname_android_g, "2.3", "2.3.3"),
            VersionCommentFormatter(
                VERSION_CODES.GINGERBREAD, VERSION_CODES.GINGERBREAD_MR1,
                "2.3", "2.3.3"
            ),
            com.android_g.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_G
        ),
        EggGroup(
            0,
            Egg(
                R.drawable.ic_android_froyo,
                R.string.nickname_android_froyo,
                VersionFormatter(R.string.nickname_android_froyo, "2.2"),
                VersionCommentFormatter(VERSION_CODES.FROYO, "2.2")
            ),
            Egg(
                R.drawable.ic_android_eclair,
                R.string.nickname_android_eclair,
                VersionFormatter(R.string.nickname_android_eclair, "2.0", "2.1"),
                VersionCommentFormatter(
                    VERSION_CODES.ECLAIR, VERSION_CODES.ECLAIR_MR1,
                    "2.0", "2.1"
                )
            ),
            Egg(
                R.drawable.ic_android_donut,
                R.string.nickname_android_donut,
                VersionFormatter(R.string.nickname_android_donut, "1.6"),
                VersionCommentFormatter(VERSION_CODES.DONUT, "1.6")
            ),
            Egg(
                R.drawable.ic_android_cupcake,
                R.string.nickname_android_cupcake,
                VersionFormatter(R.string.nickname_android_cupcake, "1.5"),
                VersionCommentFormatter(VERSION_CODES.CUPCAKE, "1.5")
            ),
            Egg(
                R.drawable.ic_android_classic,
                R.string.nickname_android_petit_four,
                VersionFormatter(R.string.nickname_android_petit_four, "1.1"),
                VersionCommentFormatter(VERSION_CODES.BASE_1_1, "1.1")
            ),
            Egg(
                R.drawable.ic_android_classic,
                R.string.nickname_android_base,
                VersionFormatter(R.string.nickname_android_base, "1.0"),
                VersionCommentFormatter(VERSION_CODES.BASE, "1.0")
            )
        ),
        Wavy(R.drawable.ic_wavy_line),
    )
}