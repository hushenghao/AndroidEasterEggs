package com.dede.android_eggs.main.entity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import com.dede.android_eggs.ui.adapter.VType

data class Egg(
    @DrawableRes val iconRes: Int,
    @StringRes val androidRes: Int,
    @StringRes val eggNameRes: Int,
    val versionCommentFormatter: CharSequenceFormatter,
    val targetClass: Class<out Activity>? = null,
    val supportAdaptiveIcon: Boolean = false,
    val key: String? = null,
    val extras: Bundle? = null,
    private val itemType: Int = VIEW_TYPE_EGG,
) : VType {

    class CharSequenceFormatter(@StringRes val resId: Int, vararg formatArgs: Any) {

        private val args: Array<out Any> = formatArgs
        fun format(context: Context): CharSequence {
            return context.getString(resId, *args)
        }
    }

    companion object {
        const val VIEW_TYPE_EGG = 0
        const val VIEW_TYPE_WAVY = -1
        const val VIEW_TYPE_PREVIEW = 1
        const val VIEW_TYPE_EGG_GROUP = 2
    }

    override val viewType: Int = itemType
}

class EggGroup(
    @MenuRes val groupMenu: Int,
    val child: List<Egg>,
    var selectedIndex: Int = child.size - 1,
) : VType {

    constructor(@MenuRes menuRes: Int, selectedIndex: Int, vararg child: Egg) : this(
        menuRes,
        child.toList(),
        selectedIndex
    )

    val selectedEgg: Egg get() = child[selectedIndex]

    override val viewType: Int
        get() = Egg.VIEW_TYPE_EGG_GROUP
}

class Wavy(val wavyRes: Int, val repeat: Boolean = false) : VType {
    override val viewType: Int = Egg.VIEW_TYPE_WAVY
}
