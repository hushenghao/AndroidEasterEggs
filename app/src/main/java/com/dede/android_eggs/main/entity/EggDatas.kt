package com.dede.android_eggs.main.entity

import android.os.Build.VERSION_CODES
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg.CharSequenceFormatter


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

    val eggList = listOf(
        Wavy(R.drawable.ic_wavy_line),
        Egg(
            com.android_u.egg.R.drawable.u_android14_patch_adaptive,
            R.string.title_android_u,
            R.string.u_egg_name_override,
            CharSequenceFormatter(R.string.version_comment_once, 34, "14"),
            com.android_u.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_U
        ),
        Egg(
            R.drawable.ic_android_tiramisu,
            R.string.title_android_t,
            com.android_t.egg.R.string.t_egg_name,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.TIRAMISU,
                "13"
            ),
            com.android_t.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_T
        ),
        Egg(
            R.drawable.ic_android_s,
            R.string.title_android_s,
            com.android_s.egg.R.string.s_egg_name,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.S, VERSION_CODES.S_V2,
                "12", "12L"
            ),
            com.android_s.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_S
        ),
        Egg(
            com.android_r.egg.R.drawable.r_icon,
            R.string.title_android_r,
            com.android_r.egg.R.string.r_egg_name,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.R,
                "11"
            ),
            com.android_r.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_R
        ),
        Egg(
            com.android_q.egg.R.drawable.q_icon,
            R.string.title_android_q,
            com.android_q.egg.R.string.q_egg_name,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.Q,
                "10"
            ),
            com.android_q.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_Q
        ),
        Egg(
            com.android_p.egg.R.drawable.p_icon,
            R.string.title_android_p,
            com.android_p.egg.R.string.p_app_name,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.P,
                "9"
            ),
            com.android_p.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_P
        ),
        EggGroup(
            R.menu.menu_eggs_o_group,
            1,
            Egg(
                R.drawable.ic_android_oreo,
                R.string.title_android_o_1,
                com.android_o.egg.R.string.o_app_name,
                CharSequenceFormatter(
                    R.string.version_comment_range,
                    VERSION_CODES.O, VERSION_CODES.O_MR1,
                    "8.0", "8.1"
                ),
                com.android_o.egg.PlatLogoActivity.Point1::class.java,
                true,
                KEY_EGG_O_1,
            ),
            Egg(
                R.drawable.ic_android_oreo,
                R.string.title_android_o,
                com.android_o.egg.R.string.o_app_name,
                CharSequenceFormatter(
                    R.string.version_comment_range,
                    VERSION_CODES.O, VERSION_CODES.O_MR1,
                    "8.0", "8.1"
                ),
                com.android_o.egg.PlatLogoActivity::class.java,
                true,
                KEY_EGG_O
            ),
        ),
        Egg(
            R.drawable.ic_android_nougat,
            R.string.title_android_n,
            com.android_n.egg.R.string.n_app_name,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.N, VERSION_CODES.N_MR1,
                "7.0", "7.1"
            ),
            com.android_n.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_N
        ),
        Egg(
            R.drawable.ic_android_marshmallow,
            R.string.title_android_m,
            com.android_m.egg.R.string.m_mland,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.M,
                "6.0"
            ),
            com.android_m.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_M
        ),
        Egg(
            R.drawable.ic_android_lollipop,
            R.string.title_android_l,
            com.android_l.egg.R.string.l_lland,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1,
                "5.0", "5.1"
            ),
            com.android_l.egg.PlatLogoActivity::class.java,
            true,
            KEY_EGG_L
        ),
        Egg(
            R.drawable.ic_android_kitkat,
            R.string.title_android_k,
            com.android_k.egg.R.string.k_dessert_case,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.KITKAT, VERSION_CODES.KITKAT_WATCH,
                "4.4", "4.4W"
            ),
            com.android_k.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_K
        ),
        Egg(
            R.drawable.ic_android_jelly_bean,
            R.string.title_android_j,
            com.android_j.egg.R.string.j_egg_name,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.JELLY_BEAN, VERSION_CODES.JELLY_BEAN_MR2,
                "4.1", "4.3"
            ),
            com.android_j.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_J
        ),
        Egg(
            R.drawable.ic_android_ics,
            R.string.title_android_i,
            com.android_i.egg.R.string.i_egg_name,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.ICE_CREAM_SANDWICH, VERSION_CODES.ICE_CREAM_SANDWICH_MR1,
                "4.0", "4.0.3"
            ),
            com.android_i.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_I
        ),
        Egg(
            R.drawable.ic_android_honeycomb,
            R.string.title_android_h,
            com.android_h.egg.R.string.h_egg_name,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.HONEYCOMB, VERSION_CODES.HONEYCOMB_MR2,
                "3.0", "3.2"
            ),
            com.android_h.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_H
        ),
        Egg(
            R.drawable.ic_android_gingerbread,
            R.string.title_android_g,
            com.android_g.egg.R.string.g_egg_name,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.GINGERBREAD, VERSION_CODES.GINGERBREAD_MR1,
                "2.3", "2.3.3"
            ),
            com.android_g.egg.PlatLogoActivity::class.java,
            false,
            KEY_EGG_G
        ),
        Wavy(R.drawable.ic_wavy_line),
        Egg(
            R.drawable.ic_android_froyo,
            R.string.title_android_froyo,
            R.string.summary_android_froyo,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.FROYO,
                "2.2"
            )
        ),
        Egg(
            R.drawable.ic_android_eclair,
            R.string.title_android_eclair,
            R.string.summary_android_eclair,
            CharSequenceFormatter(
                R.string.version_comment_range,
                VERSION_CODES.ECLAIR, VERSION_CODES.ECLAIR_MR1,
                "2.0", "2.1"
            )
        ),
        Egg(
            R.drawable.ic_android_donut,
            R.string.title_android_donut,
            R.string.summary_android_donut,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.DONUT,
                "1.6"
            )
        ),
        Egg(
            R.drawable.ic_android_cupcake,
            R.string.title_android_cupcake,
            R.string.summary_android_cupcake,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.CUPCAKE,
                "1.5"
            )
        ),
        Egg(
            R.drawable.ic_android_classic,
            R.string.title_android_petit_four,
            R.string.summary_android_petit_four,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.BASE_1_1,
                "1.1"
            )
        ),
        Egg(
            R.drawable.ic_android_classic,
            R.string.title_android_base,
            R.string.summary_android_base,
            CharSequenceFormatter(
                R.string.version_comment_once,
                VERSION_CODES.BASE,
                "1.0"
            )
        ),
        Wavy(R.drawable.ic_wavy_line),
    )
}