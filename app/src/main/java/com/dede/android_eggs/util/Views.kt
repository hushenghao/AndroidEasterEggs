package com.dede.android_eggs.util

import android.content.Context
import android.content.res.Resources
import android.util.LayoutDirection

val systemIsRtl: Boolean
    get() = Resources.getSystem().configuration.layoutDirection == LayoutDirection.RTL

val Context.isRtl: Boolean
    get() = resources.configuration.layoutDirection == LayoutDirection.RTL
