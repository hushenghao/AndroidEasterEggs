package com.dede.android_eggs.main.entity

import androidx.core.os.bundleOf
import com.dede.android_eggs.R


object EggDatas {

    private const val EXTRA_O_POINT = "isOreoPoint"

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
        com.android_t.egg.PlatLogoSnapshotProvider(),
        com.android_s.egg.PlatLogoSnapshotProvider(),
        com.android_r.egg.PlatLogoSnapshotProvider(),
        com.android_q.egg.PlatLogoSnapshotProvider(),
        com.android_p.egg.PlatLogoSnapshotProvider(),
        com.android_o.egg.PlatLogoSnapshotProvider(true),
        com.android_o.egg.PlatLogoSnapshotProvider(false),
        com.android_n.egg.PlatLogoSnapshotProvider(),
        com.android_m.egg.PlatLogoSnapshotProvider(),
        com.android_l.egg.PlatLogoSnapshotProvider(),
        com.android_k.egg.PlatLogoSnapshotProvider(),
        com.android_j.egg.PlatLogoSnapshotProvider(),
        com.android_i.egg.PlatLogoSnapshotProvider(),
        com.android_h.egg.PlatLogoSnapshotProvider(),
        com.android_g.egg.PlatLogoSnapshotProvider(),
    )

    val eggList = listOf(
        Egg(
            R.drawable.ic_android_udc,
            R.string.title_android_u,
            R.string.title_android_u,
            R.string.version_comment_android_u,
            -1,
            true,
            itemType = Egg.VIEW_TYPE_PREVIEW
        ),
        Wavy(R.drawable.ic_wavy_line),
        Egg(
            R.drawable.ic_android_tiramisu,
            R.string.title_android_t,
            com.android_t.egg.R.string.t_egg_name,
            R.string.version_comment_android_t,
            R.string.target_class_android_t,
            true,
            KEY_EGG_T
        ),
        Egg(
            R.drawable.ic_android_s,
            R.string.title_android_s,
            com.android_s.egg.R.string.s_egg_name,
            R.string.version_comment_android_s,
            R.string.target_class_android_s,
            true,
            KEY_EGG_S
        ),
        Egg(
            com.android_r.egg.R.drawable.r_icon,
            R.string.title_android_r,
            com.android_r.egg.R.string.r_egg_name,
            R.string.version_comment_android_r,
            R.string.target_class_android_r,
            true,
            KEY_EGG_R
        ),
        Egg(
            com.android_q.egg.R.drawable.q_icon,
            R.string.title_android_q,
            com.android_q.egg.R.string.q_egg_name,
            R.string.version_comment_android_q,
            R.string.target_class_android_q,
            true,
            KEY_EGG_Q
        ),
        Egg(
            com.android_p.egg.R.drawable.p_icon,
            R.string.title_android_p,
            com.android_p.egg.R.string.p_app_name,
            R.string.version_comment_android_p,
            R.string.target_class_android_p,
            true,
            KEY_EGG_P
        ),
        Egg(
            R.drawable.ic_android_oreo,
            R.string.title_android_o_1,
            com.android_o.egg.R.string.o_app_name,
            R.string.version_comment_android_o,
            R.string.target_class_android_o,
            true,
            KEY_EGG_O_1,
            bundleOf(EXTRA_O_POINT to true)
        ),
        Egg(
            R.drawable.ic_android_oreo,
            R.string.title_android_o,
            com.android_o.egg.R.string.o_app_name,
            R.string.version_comment_android_o,
            R.string.target_class_android_o,
            true,
            KEY_EGG_O
        ),
        Egg(
            R.drawable.ic_android_nougat,
            R.string.title_android_n,
            com.android_n.egg.R.string.n_app_name,
            R.string.version_comment_android_n,
            R.string.target_class_android_n,
            true,
            KEY_EGG_N
        ),
        Egg(
            R.drawable.ic_android_marshmallow,
            R.string.title_android_m,
            com.android_m.egg.R.string.m_mland,
            R.string.version_comment_android_m,
            R.string.target_class_android_m,
            true,
            KEY_EGG_M
        ),
        Egg(
            R.drawable.ic_android_lollipop,
            R.string.title_android_l,
            com.android_l.egg.R.string.l_lland,
            R.string.version_comment_android_l,
            R.string.target_class_android_l,
            true,
            KEY_EGG_L
        ),
        Egg(
            R.drawable.ic_android_kitkat,
            R.string.title_android_k,
            com.android_k.egg.R.string.k_dessert_case,
            R.string.version_comment_android_k,
            R.string.target_class_android_k,
            false,
            KEY_EGG_K
        ),
        Egg(
            R.drawable.ic_android_jelly_bean,
            R.string.title_android_j,
            com.android_j.egg.R.string.j_egg_name,
            R.string.version_comment_android_j,
            R.string.target_class_android_j,
            false,
            KEY_EGG_J
        ),
        Egg(
            R.drawable.ic_android_ics,
            R.string.title_android_i,
            com.android_i.egg.R.string.i_egg_name,
            R.string.version_comment_android_i,
            R.string.target_class_android_i,
            false,
            KEY_EGG_I
        ),
        Egg(
            R.drawable.ic_android_honeycomb,
            R.string.title_android_h,
            com.android_h.egg.R.string.h_egg_name,
            R.string.version_comment_android_h,
            R.string.target_class_android_h,
            false,
            KEY_EGG_H
        ),
        Egg(
            R.drawable.ic_android_gingerbread,
            R.string.title_android_g,
            com.android_g.egg.R.string.g_egg_name,
            R.string.version_comment_android_g,
            R.string.target_class_android_g,
            false,
            KEY_EGG_G
        ),
        Wavy(R.drawable.ic_wavy_line),
        Egg(
            R.drawable.ic_android_froyo,
            R.string.title_android_froyo,
            R.string.summary_android_froyo,
            R.string.version_comment_android_froyo
        ),
        Egg(
            R.drawable.ic_android_eclair,
            R.string.title_android_eclair,
            R.string.summary_android_eclair,
            R.string.version_comment_android_eclair
        ),
        Egg(
            R.drawable.ic_android_donut,
            R.string.title_android_donut,
            R.string.summary_android_donut,
            R.string.version_comment_android_donut
        ),
        Egg(
            R.drawable.ic_android_cupcake,
            R.string.title_android_cupcake,
            R.string.summary_android_cupcake,
            R.string.version_comment_android_cupcake
        ),
        Egg(
            R.drawable.ic_android_classic,
            R.string.title_android_petit_four,
            R.string.summary_android_petit_four,
            R.string.version_comment_android_petit_four
        ),
        Egg(
            R.drawable.ic_android_classic,
            R.string.title_android_base,
            R.string.title_android_base,
            R.string.version_comment_android_base
        ),
        Wavy(R.drawable.ic_wavy_line_1, true),
        Footer()
    )
}