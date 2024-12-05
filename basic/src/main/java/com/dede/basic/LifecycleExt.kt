@file:Suppress("DEPRECATION")

package com.dede.basic

import android.annotation.SuppressLint
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
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
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

val Activity.androidLifecycleOwner: LifecycleOwner
    get() {
        if (this is ComponentActivity) {
            return this
        }
        return LifecycleFragment.injectIfNeededIn(this)
    }

val Activity.androidSavedStateOwner: SavedStateRegistryOwner
    get() {
        if (this is ComponentActivity) {
            return this
        }
        return LifecycleFragment.injectIfNeededIn(this)
    }

val Activity.androidLifecycle: Lifecycle
    get() {
        if (this is ComponentActivity) {
            return lifecycle
        }
        return LifecycleFragment.injectIfNeededIn(this).lifecycle
    }

fun Lifecycle.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = coroutineScope.launch(context, start, block)

@Suppress("OVERRIDE_DEPRECATION")
@SuppressLint("ValidFragment")
internal class LifecycleFragment : android.app.Fragment(), LifecycleOwner, SavedStateRegistryOwner {

    companion object {
        fun injectIfNeededIn(activity: Activity): LifecycleFragment {
            // com.bumptech.glide.manager.RequestManagerRetriever#get(android.app.Activity)
            val fm = activity.fragmentManager
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
            return current
        }
    }

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    init {
        savedStateRegistryController.performAttach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        savedStateRegistryController.performRestore(savedInstanceState)
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

    override fun onSaveInstanceState(outState: Bundle) {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        super.onSaveInstanceState(outState)
        savedStateRegistryController.performSave(outState)
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
}