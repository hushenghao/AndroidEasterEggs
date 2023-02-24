package com.dede.android_eggs.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.FontIconsDrawable
import com.google.android.material.materialswitch.MaterialSwitch

class NightModeSwitchPreference(context: Context, attrs: AttributeSet?) :
    SwitchPreferenceCompat(context, attrs) {

    init {
        widgetLayoutResource = R.layout.layout_widget_night_mode_switch
        switchTextOff = "\ue1ab"
        switchTextOn = "\ue3a9"
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        val switch = holder.findViewById(R.id.switchWidget) as MaterialSwitch
        switch.setSwitchTypeface(FontIconsDrawable.ICONS_TYPEFACE)
        super.onBindViewHolder(holder)
    }
}