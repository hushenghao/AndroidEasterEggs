package com.dede.android_eggs.main

import android.content.res.Resources
import android.view.View
import android.window.BackEvent
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dede.android_eggs.util.applyIf

class BackPressedHandler(private val host: AppCompatActivity) :
    OnBackPressedCallback(enabled = true), DefaultLifecycleObserver {

    private val maxXShift = Resources.getSystem().displayMetrics.widthPixels / 20
    private val content: View by lazy { host.findViewById(android.R.id.content) }
    private var isStopped = false

    fun register() {
        host.onBackPressedDispatcher.addCallback(this)
        host.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (!isStopped) return
        isStopped = false
        // onRestart
        restoreContentState(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        isStopped = true
    }


    private fun restoreContentState(delay: Boolean) {
        content.animate()
            .scaleX(1f)
            .scaleY(1f)
            .translationX(0f)
            .translationY(0f)
            .setDuration(300L)
            .applyIf(delay) {
                startDelay = 200L
            }
            .start()
    }

    override fun handleOnBackStarted(backEvent: BackEvent) {
    }

    @RequiresApi(34)
    override fun handleOnBackProgressed(backEvent: BackEvent) {
        when (backEvent.swipeEdge) {
            BackEvent.EDGE_LEFT ->
                content.translationX = backEvent.progress * maxXShift

            BackEvent.EDGE_RIGHT ->
                content.translationX = -(backEvent.progress * maxXShift)
        }
        content.scaleX = 1F - (0.1F * backEvent.progress)
        content.scaleY = 1F - (0.1F * backEvent.progress)
    }

    override fun handleOnBackCancelled() {
        restoreContentState(false)
    }

    override fun handleOnBackPressed() {
        isEnabled = false
        @Suppress("DEPRECATION")
        host.onBackPressed()
        isEnabled = true
    }

}