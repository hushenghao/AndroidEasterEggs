@file:JvmName("DrawableKt")

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat

@SuppressLint("DiscouragedApi")
@Throws(Resources.NotFoundException::class)
fun Context.getSystemColor(resName: String): Int {
    val id = resources.getIdentifier(resName, "color", "android")
    return ContextCompat.getColor(this, id)
}
