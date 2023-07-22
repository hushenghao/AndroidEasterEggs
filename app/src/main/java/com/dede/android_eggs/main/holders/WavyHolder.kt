package com.dede.android_eggs.main.holders

import android.view.View
import android.widget.ImageView
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Wavy
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.util.createRepeatWavyDrawable
import com.dede.basic.requireDrawable

@VHType(viewType = Egg.VIEW_TYPE_WAVY)
class WavyHolder(view: View) : VHolder<Wavy>(view) {

    private val imageView = itemView.findViewById<ImageView>(R.id.iv_icon)

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onBindViewHolder(wavy: Wavy) {
        if (!wavy.repeat) {
            imageView.scaleType = ImageView.ScaleType.CENTER
            imageView.setImageDrawable(context.requireDrawable(wavy.wavyRes))
        } else {
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageDrawable(createRepeatWavyDrawable(context, wavy.wavyRes))
        }
    }
}