package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import com.dede.android_eggs.R
import com.google.android.material.internal.ContextUtils


@Suppress("UNCHECKED_CAST")
@SuppressLint("RestrictedApi")
fun <T : Activity> Context.getActivity(): T? {
    return ContextUtils.getActivity(this) as? T
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resId, duration).show()
}

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.createChooser(target: Intent): Intent {
    return Intent.createChooser(target, getString(R.string.title_open_with))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
