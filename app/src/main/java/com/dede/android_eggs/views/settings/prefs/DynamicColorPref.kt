package com.dede.android_eggs.views.settings.prefs

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.views.settings.SettingPref
import com.dede.android_eggs.views.settings.SettingPref.Op.Companion.isEnable
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions


class DynamicColorPref : SettingPref(
    "pref_key_dynamic_color",
    listOf(
        Op(Op.ON, titleRes = R.string.preference_on, iconUnicode = Icons.Rounded.palette),
        Op(Op.OFF, titleRes = R.string.preference_off)
    ),
    if (DynamicColors.isDynamicColorAvailable()) Op.ON else Op.OFF
), DynamicColors.Precondition, DynamicColors.OnAppliedCallback {

    override val titleRes: Int
        get() = R.string.pref_title_dynamic_color
    override val enable: Boolean
        get() = DynamicColors.isDynamicColorAvailable()

    private var isApplied = false
    private var isOptionOn = false

    override fun apply(context: Context, option: Op) {
        isOptionOn = option.isEnable()
        if (isOptionOn && !isApplied) {
            DynamicColors.applyToActivitiesIfAvailable(
                context.applicationContext as Application,
                DynamicColorsOptions.Builder()
                    .setPrecondition(this)
                    .setOnAppliedCallback(this)
                    .build()
            )
            isApplied = true
        }
        recreateActivityIfPossible(context)
    }

    override fun shouldApplyDynamicColors(activity: Activity, theme: Int): Boolean {
        if (activity is AppCompatActivity) {
            return isOptionOn
        }
        return false
    }

    override fun onApplied(activity: Activity) {
        HarmonizedColors.applyToContextIfAvailable(
            activity, HarmonizedColorsOptions.createMaterialDefaults()
        )
    }
}