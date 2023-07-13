package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.google.android.material.internal.ContextUtils


@Suppress("UNCHECKED_CAST")
@SuppressLint("RestrictedApi")
fun <T : Activity> Context.getActivity(): T? {
    return ContextUtils.getActivity(this) as? T
}