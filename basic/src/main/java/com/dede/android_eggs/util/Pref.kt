package com.dede.android_eggs.util

import android.content.Context
import android.content.SharedPreferences

val Context.pref: SharedPreferences
    get() {
        return applicationContext.getSharedPreferences(
            applicationContext.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
    }
