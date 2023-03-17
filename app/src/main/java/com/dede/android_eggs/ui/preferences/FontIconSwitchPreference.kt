package com.dede.android_eggs.ui.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.google.android.material.materialswitch.MaterialSwitch

class FontIconSwitchPreference(context: Context, attrs: AttributeSet?) :
    SwitchPreferenceCompat(context, attrs) {

    init {
        widgetLayoutResource = R.layout.layout_widget_font_icon_material_switch
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        val switch = holder.findViewById(R.id.switchWidget) as MaterialSwitch
        switch.setSwitchTypeface(FontIconsDrawable.ICONS_TYPEFACE)
        super.onBindViewHolder(holder)
    }
}