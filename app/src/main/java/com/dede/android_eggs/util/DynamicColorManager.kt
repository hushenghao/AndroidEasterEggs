package com.dede.android_eggs.util

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions
import com.google.android.material.internal.ContextUtils

class DynamicColorManager : DynamicColors.Precondition, DynamicColors.OnAppliedCallback {

    companion object {
        const val KEY_DYNAMIC_COLOR = "key_dynamic_color"

        fun apply(context: Context) {
            DynamicColorManager().apply(context)
        }

        fun isDynamicColorAvailable(): Boolean {
            return DynamicColors.isDynamicColorAvailable()
        }
    }


    fun setDynamicColorEnable(context: Context, enable: Boolean) {
        if (isDynamicColorEnable(context) == enable) {
            return
        }
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit(true) {
            putBoolean(KEY_DYNAMIC_COLOR, enable)
        }
        apply(context)
    }

    fun isDynamicColorEnable(context: Context): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getBoolean(KEY_DYNAMIC_COLOR, isDynamicColorAvailable())
    }

    private fun apply(context: Context) {
        DynamicColors.applyToActivitiesIfAvailable(
            context.applicationContext as Application,
            DynamicColorsOptions.Builder()
                .setPrecondition(this)
                .setOnAppliedCallback(this)
                .build()
        )
        ContextUtils.getActivity(context)?.recreate()
    }

    override fun shouldApplyDynamicColors(activity: Activity, theme: Int): Boolean {
        if (activity is AppCompatActivity) {
            return isDynamicColorEnable(activity)
        }
        return false
    }

    override fun onApplied(activity: Activity) {
        HarmonizedColors.applyToContextIfAvailable(
            activity, HarmonizedColorsOptions.createMaterialDefaults()
        )
    }


}