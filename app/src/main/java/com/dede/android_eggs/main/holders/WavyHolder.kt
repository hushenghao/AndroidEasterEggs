package com.dede.android_eggs.main.holders

import android.content.Context
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.dispose
import coil.load
import coil.size.Size
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Wavy
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.util.resolveColor
import com.dede.basic.requireDrawable

@VHType(viewType = Egg.VIEW_TYPE_WAVY)
class WavyHolder(view: View) : VHolder<Wavy>(view) {

    private val imageView = itemView.findViewById<ImageView>(R.id.iv_icon)

    private fun getRepeatWavyDrawable(context: Context, wavyRes: Int): Drawable {
        val bitmap = context.requireDrawable(wavyRes).toBitmap()
        return BitmapDrawable(context.resources, bitmap).apply {
            setTileModeXY(Shader.TileMode.REPEAT, null)
            setTint(context.resolveColor(com.google.android.material.R.attr.colorSecondaryContainer))
        }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onBindViewHolder(wavy: Wavy) {
        imageView.dispose()
        imageView.setImageDrawable(null)
        imageView.background = null
        if (!wavy.repeat) {
            imageView.load(wavy.wavyRes) {
                size(Size.ORIGINAL)
            }
            return
        }
        imageView.background = getRepeatWavyDrawable(imageView.context, wavy.wavyRes)
    }
}