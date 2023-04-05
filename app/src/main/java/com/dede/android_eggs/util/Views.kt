package com.dede.android_eggs.util

import android.view.View
import androidx.appcompat.widget.ViewUtils

val View.isLayoutRtl: Boolean
    get() = @Suppress("RestrictedApi") ViewUtils.isLayoutRtl(this)

