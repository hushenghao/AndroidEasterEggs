@file:Suppress("DEPRECATION")

package com.dede.basic

import android.app.Activity
import android.app.FragmentManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


private const val TAG_FRAGMENT = "Lifecycle"
private const val ID_REMOVE = 1
private val pendingLifecycleFragments = HashMap<FragmentManager, LifecycleFragment>()
private val handler = Handler(Looper.getMainLooper()) { msg ->
    when (msg.what) {
        ID_REMOVE -> {
            pendingLifecycleFragments.remove(msg.obj as FragmentManager)
        }
    }
    true
}

val Activity.androidLifecycle: Lifecycle
    get() {
        if (this is ComponentActivity) {
            return lifecycle
        }
        // Can make use of [androidx.lifecycle.ReportFragment],
        // But the API may change.
        //return ReportFragmentAccessor.injectIfNeededIn(this)

        // com.bumptech.glide.manager.RequestManagerRetriever#get(android.app.Activity)
        val fm = fragmentManager
        var current = fm.findFragmentByTag(TAG_FRAGMENT) as? LifecycleFragment
        if (current == null) {
            current = pendingLifecycleFragments[fm]
            if (current == null) {
                current = LifecycleFragment()
                pendingLifecycleFragments[fm] = current
                fm.beginTransaction().add(current, TAG_FRAGMENT).commitAllowingStateLoss()
                handler.obtainMessage(ID_REMOVE, fm).sendToTarget()
            }
        }
        return current.lifecycle
    }

fun Lifecycle.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = coroutineScope.launch(context, start, block)

internal class LifecycleFragment : android.app.Fragment(), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}