package com.dede.android_eggs.util

import android.content.Context
import android.content.res.Resources
import android.util.LayoutDirection
import android.view.View
import androidx.core.view.ViewCompat

val systemIsRtl: Boolean
    get() = Resources.getSystem().configuration.layoutDirection == LayoutDirection.RTL

val Context.isRtl: Boolean
    get() = resources.configuration.layoutDirection == LayoutDirection.RTL

val View.isRtl: Boolean
    get() = context.isRtl
//    get() = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL