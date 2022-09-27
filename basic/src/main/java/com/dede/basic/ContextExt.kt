@file:JvmName("ContextExt")

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

/**
 * Created by shhu on 2022/9/27 14:26.
 *
 * @author shhu
 * @since 2022/9/27
 */

val globalContext: Context get() = GlobalContext.globalContext

@SuppressLint("StaticFieldLeak")
object GlobalContext {
    lateinit var globalContext: Context
        private set

    fun init(context: Context) {
        globalContext = context.applicationContext
    }
}

val Int.string: String get() = globalContext.getString(this)

val Int.color: Int get() = ContextCompat.getColor(globalContext, this)

val Int.drawable: Drawable get() = globalContext.requireDrawable(this)