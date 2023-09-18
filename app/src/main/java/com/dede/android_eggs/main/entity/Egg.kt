package com.dede.android_eggs.main.entity

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.adapter.VType
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.basic.requireDrawable

data class Egg(
    @DrawableRes val iconRes: Int,
    @StringRes val eggNameRes: Int,
    val versionFormatter: VersionFormatter,
    val versionCommentFormatter: VersionCommentFormatter,
    val targetClass: Class<out Activity>? = null,
    val supportAdaptiveIcon: Boolean = false,
    val key: String? = null,
    val extras: Bundle? = null,
    private val itemType: Int = VIEW_TYPE_EGG,
) : VType {

    val id = ViewCompat.generateViewId()

    class VersionFormatter(
        @StringRes val nicknameRes: Int,
        vararg versionNames: CharSequence,
    ) {

        private val versionNames: Array<out CharSequence> = versionNames

        fun format(context: Context): CharSequence {
            val sb = StringBuilder()
            val enDash = context.getString(R.string.char_en_dash)
            val nickname = context.getString(nicknameRes)
            versionNames.joinTo(sb, prefix = "Android ", separator = enDash, postfix = " (")
            sb.append(nickname)
                .append(")")
            return sb
        }
    }

    class VersionCommentFormatter(
        private val versionCode1: Int,
        private val versionCode2: Int,
        private val versionName1: CharSequence,
        private val versionName2: CharSequence,
    ) {

        constructor(versionCode: Int, versionName: CharSequence) :
                this(versionCode, versionCode, versionName, versionName)

        fun format(context: Context): CharSequence {
            val sb = StringBuilder("API ")
                .append(versionCode1)
            if (versionCode1 == versionCode2) {
                sb.append("\nAndroid ")
                    .append(versionName1)
            } else {
                val enDash = context.getString(R.string.char_en_dash)
                sb.append(enDash)
                    .append(versionCode2.toString())
                    .append("\nAndroid ")
                    .append(versionName1)
                    .append(enDash)
                    .append(versionName2)
            }
            return sb
        }
    }

    companion object {
        const val VIEW_TYPE_EGG = 0
        const val VIEW_TYPE_WAVY = -1
        const val VIEW_TYPE_PREVIEW = 1
        const val VIEW_TYPE_EGG_GROUP = 2

        fun Egg.getIcon(context: Context): Drawable {
            if (supportAdaptiveIcon) {
                val pathStr = IconShapePref.getMaskPath(context)
                return AlterableAdaptiveIconDrawable(context, iconRes, pathStr)
            }
            return context.requireDrawable(iconRes)
        }
    }

    override val viewType: Int = itemType
}

class EggGroup(
    val child: List<Egg>,
    var selectedIndex: Int = 0,
) : VType {

    constructor(selectedIndex: Int, vararg child: Egg) : this(
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
