package com.dede.android_eggs.crash

import android.os.DeadObjectException

/**
 * A failure the system raised against this app. It reaches the uncaught exception handler,
 * but says nothing about this app, so it is only written to logcat instead of being shown
 * as a crash of this app.
 *
 * [type] is matched anywhere in the cause chain, [framePrefixes] against the matched
 * throwable's own stack trace: the type alone would also match a throwable of the same type
 * raised by app code, since anything can construct one.
 */
internal class IgnoredException(
    private val type: Class<out Throwable>,
    private val framePrefixes: List<String>,
) {

    fun matches(throwable: Throwable): Boolean {
        if (!type.isInstance(throwable)) {
            return false
        }
        return throwable.stackTrace.any { frame ->
            framePrefixes.any { frame.className.startsWith(it) }
        }
    }
}

internal val IGNORED_EXCEPTIONS: List<IgnoredException> = listOf(
    // The binder object died while it was being called: System UI or a system service is
    // restarting. The framework only ever creates a DeadObjectException inside
    // android.os.BinderProxy (see android_util_Binder.cpp), so that frame is what tells a
    // failed transaction apart from app code throwing the same type. See #709, #729,
    // #860, #986.
    IgnoredException(
        type = DeadObjectException::class.java,
        framePrefixes = listOf("android.os.BinderProxy"),
    ),
)

internal fun Throwable.isIgnoredException(): Boolean {
    val visited = HashSet<Throwable>()
    var current: Throwable = this
    while (true) {
        if (IGNORED_EXCEPTIONS.any { it.matches(current) }) {
            return true
        }
        // A cause chain is allowed to loop, stop as soon as a throwable comes around again.
        if (!visited.add(current)) {
            return false
        }
        current = current.cause ?: return false
    }
}
