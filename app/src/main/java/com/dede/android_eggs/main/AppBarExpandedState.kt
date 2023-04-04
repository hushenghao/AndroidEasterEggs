package com.dede.android_eggs.main

import android.app.Activity
import android.os.Bundle
import androidx.annotation.IdRes
import com.google.android.material.appbar.AppBarLayout

/**
 * Fix AppBarLayout Expanded state error
 */
class AppBarExpandedState(
    private val activity: Activity,
    @IdRes private val id: Int,
    private val animate: Boolean = false,
    private val default: Boolean = true,
) : AppBarLayout.OnOffsetChangedListener {

    companion object {
        private const val STATE_KEY = "appbar_expanded_state"
    }

    private lateinit var appBarLayout: AppBarLayout
    private var isExpanded: Boolean = default

    fun restore(savedInstanceState: Bundle?) {
        appBarLayout = activity.findViewById(id)
        appBarLayout.addOnOffsetChangedListener(this)
        if (savedInstanceState != null) {
            isExpanded = savedInstanceState.getBoolean(STATE_KEY, default)
        }
        appBarLayout.setExpanded(isExpanded, animate)
    }

    fun saveState(outState: Bundle) {
        outState.putBoolean(STATE_KEY, isExpanded)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        isExpanded = verticalOffset >= 0
    }
}