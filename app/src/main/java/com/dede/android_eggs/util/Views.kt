package com.dede.android_eggs.util

import android.content.res.Resources
import android.util.LayoutDirection

val isRtl: Boolean
    get() = Resources.getSystem().configuration.layoutDirection == LayoutDirection.RTL

