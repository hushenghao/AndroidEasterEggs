package com.dede.android_eggs.util

import android.content.res.Resources
import android.util.LayoutDirection
import android.view.View
import androidx.core.view.ViewCompat

val isRtl: Boolean
    get() = Resources.getSystem().configuration.layoutDirection == LayoutDirection.RTL

val View.isRtl: Boolean
    get() = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL