package com.dede.android_eggs.views.settings.prefs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.settings.SettingPref
import com.dede.android_eggs.views.settings.SettingsPrefs
import com.dede.basic.dp


class IconShapePref : SettingPref(
    "pref_key_override_icon_shape",
    listOf(
        Op(0, iconRes = R.drawable.ic_android_classic),
        iconShapeOp(1),
        iconShapeOp(2),
        iconShapeOp(3),
        iconShapeOp(4),
        iconShapeOp(5),
        iconShapeOp(6),
        iconShapeOp(7),
    ),
    0
) {
    companion object {
        const val ACTION_CHANGED = "com.dede.easter_eggs.IconShapeChanged"

        private fun iconShapeOp(index: Int): Op {
            return Op(index).apply {
                iconMaker = { ctx, _ ->
                    createShapeIcon(ctx, this.value)
                }
            }
        }

        private fun createShapeIcon(context: Context, index: Int): Drawable {
            val bitmap = createBitmap(20.dp, 20.dp, Bitmap.Config.ARGB_8888)
            val pathStr = getMaskPathByIndex(context, index)
            val shapePath = AlterableAdaptiveIconDrawable.getMaskPath(
                pathStr, bitmap.width, bitmap.height
            )
            bitmap.applyCanvas {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.color = Color.BLACK
                drawPath(shapePath, paint)

                setBitmap(null)
            }
            return BitmapDrawable(context.resources, bitmap)
        }

        fun getMaskPath(context: Context): String {
            val index = IconShapePref().getSelectedOp(context)?.value ?: 0
            return getMaskPathByIndex(context, index)
        }

        private fun getMaskPathByIndex(context: Context, index: Int): String {
            val paths = context.resources.getStringArray(R.array.icon_shape_override_paths)
            return paths[index % paths.size]
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_icon_shape_override

    override fun onOptionSelected(context: Context, option: Op) {
        LocalEvent.get(context).apply {
            post(ACTION_CHANGED)
            post(SettingsPrefs.ACTION_CLOSE_SETTING)
        }
    }
}