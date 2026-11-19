package com.dede.android_eggs.baselineprofile

import android.os.SystemClock
import androidx.test.uiautomator.UiDevice

// "  ResumedActivity: ActivityRecord{261853612 u0 com.dede.android_eggs/com.android_u.egg.PlatLogoActivity t42}"
private val RESUMED_ACTIVITY =
    Regex("""(?:topResumedActivity=|[mM]?ResumedActivity: )ActivityRecord\{\S+ u\d+ (\S+) t\d+\}""")

/**
 * `am start -p <pkg> -a MAIN -c LAUNCHER` cannot resolve the launcher: its intent filter has no
 * DEFAULT category, which implicit starts require. Resolve the component and start that instead.
 */
fun UiDevice.startApp() {
    val resolved = executeShellCommand(
        "cmd package resolve-activity --brief -a android.intent.action.MAIN " +
                "-c android.intent.category.LAUNCHER $PACKAGE_NAME"
    )
    val component = resolved.lineSequence().map { it.trim() }.lastOrNull { "/" in it }
    checkNotNull(component) { "The app has no launcher activity: $resolved" }
    val output = executeShellCommand("am start -W -n $component")
    check("Error" !in output) { "Could not start the app: $output" }
}

fun UiDevice.startEgg(apiLevel: Int) {
    val output = executeShellCommand(
        "am start -W -p $PACKAGE_NAME -a android.intent.action.VIEW -d egg://easter_egg/api/$apiLevel"
    )
    check("Error" !in output) { "Could not open the egg of api/$apiLevel: $output" }
}

/** The resumed Activity as `package/Activity`, or null when it can't be read. */
fun UiDevice.resumedActivity(): String? {
    val component = RESUMED_ACTIVITY.find(executeShellCommand("dumpsys activity activities"))
        ?.groupValues?.get(1) ?: return null
    val (pkg, cls) = component.split('/', limit = 2).takeIf { it.size == 2 } ?: return component
    // The system abbreviates classes of the app's own package to ".Foo"
    return if (cls.startsWith('.')) "$pkg/$pkg$cls" else component
}

/** Polls until [expected] is resumed and returns what was resumed when it stopped looking. */
fun UiDevice.awaitResumedActivity(expected: String, timeout: Long = UI_TIMEOUT): String? {
    val deadline = SystemClock.uptimeMillis() + timeout
    var actual = resumedActivity()
    while (actual != expected && SystemClock.uptimeMillis() < deadline) {
        SystemClock.sleep(POLL_INTERVAL)
        actual = resumedActivity()
    }
    return actual
}

fun UiDevice.clearCrashLog() {
    executeShellCommand("logcat -b crash -c")
}

/** `logcat -b crash` only holds fatal exceptions, so a crash of the app shows up as its own line. */
fun UiDevice.crashLog(): String = executeShellCommand("logcat -b crash -d")
