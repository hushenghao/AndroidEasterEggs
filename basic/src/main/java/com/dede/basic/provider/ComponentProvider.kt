package com.dede.basic.provider

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


interface ComponentProvider {

    fun provideComponent(): Component

    abstract class Component constructor(
        @DrawableRes val iconRes: Int,
        @StringRes val nameRes: Int,
        @StringRes val nicknameRes: Int,
        val apiLevel: IntRange,
    ) {

        fun getSortValue(): Int {
            return apiLevel.first
        }

        override fun hashCode(): Int {
            return apiLevel.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Component) {
                return false
            }
            return apiLevel == other.apiLevel
        }

        companion object {
            @JvmStatic
            fun ComponentName.setEnable(context: Context, enable: Boolean) {
                val pm = context.packageManager
                val newState = if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                pm.setComponentEnabledSetting(this, newState, PackageManager.DONT_KILL_APP)
            }

            @JvmStatic
            fun ComponentName.isEnabled(context: Context): Boolean {
                val pm = context.packageManager
                val state = pm.getComponentEnabledSetting(this)
                return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }
        }

        constructor(
            @DrawableRes iconRes: Int,
            @StringRes nameRes: Int,
            @StringRes nicknameRes: Int,
            apiLevel: Int,
        ) : this(iconRes, nameRes, nicknameRes, apiLevel..apiLevel)

        abstract fun isSupported(): Boolean

        abstract fun isEnabled(context: Context): Boolean

        abstract fun setEnabled(context: Context, enable: Boolean)

    }
}