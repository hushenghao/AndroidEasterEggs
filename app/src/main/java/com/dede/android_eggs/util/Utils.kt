@file:JvmName("Utils")
package com.dede.android_eggs.util

import android.os.Build
import java.util.*


fun isXiaomi(): Boolean {
    return Build.MANUFACTURER.lowercase(Locale.ENGLISH) == "xiaomi"
}

