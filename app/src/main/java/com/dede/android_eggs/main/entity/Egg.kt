package com.dede.android_eggs.main.entity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dede.android_eggs.ui.adapter.VType

data class Egg(
    @DrawableRes val iconRes: Int,
    @StringRes val androidRes: Int,
    @StringRes val eggNameRes: Int,
    val versionCommentFormatter: CharSequenceFormatter,
    val targetClass: Class<out Activity>? = null,
    val supportAdaptiveIcon: Boolean = false,
    val shortcutKey: String? = null,
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