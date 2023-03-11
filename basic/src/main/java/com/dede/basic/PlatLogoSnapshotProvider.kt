package com.dede.basic

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference

abstract class PlatLogoSnapshotProvider {

    private var cache: WeakReference<View>? = null

    open fun getPlatLogoIntent(context: Context): Intent {
        val clazz = Class.forName(this.javaClass.`package`!!.name + ".PlatLogoActivity")
        return Intent(context, clazz)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS)
    }

    fun get(context: Context): View {
        var view = cache?.get()
        if (view != null) {
            (view.parent as? ViewGroup)?.removeView(view)
            return view
        }
        view = create(context)
        cache = WeakReference(view)
        return view
    }

    fun updateCache(view: View) {
        cache = WeakReference(view)
    }

    abstract fun create(context: Context): View
}