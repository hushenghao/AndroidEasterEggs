package com.dede.android_eggs.main.entity

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dede.android_eggs.ui.adapter.VType

data class Egg(
    @DrawableRes val iconRes: Int,
    @StringRes val androidRes: Int,
    @StringRes val eggNameRes: Int,
    @StringRes val versionCommentRes: Int,
    @StringRes val targetClassRes: Int = -1,
    val supportAdaptiveIcon: Boolean = false,
    val shortcutKey: String? = null,
    val extras: Bundle? = null,
    private val itemType: Int = VIEW_TYPE_EGG,
) : VType {

    companion object {
        const val VIEW_TYPE_EGG = 0
        const val VIEW_TYPE_WAVY = -1
        const val VIEW_TYPE_PREVIEW = 1
        const val VIEW_TYPE_FOOTER = -2
    }

    override val viewType: Int = itemType
}

class Wavy(val wavyRes: Int, val repeat: Boolean = false) : VType {
    override val viewType: Int = Egg.VIEW_TYPE_WAVY
}

class Footer : VType {
    override val viewType: Int = Egg.VIEW_TYPE_FOOTER
}