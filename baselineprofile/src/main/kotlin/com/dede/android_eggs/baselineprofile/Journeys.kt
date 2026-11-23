package com.dede.android_eggs.baselineprofile

import android.os.SystemClock
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

const val UI_TIMEOUT = 5_000L
const val POLL_INTERVAL = 250L

// The app has no test tags, so controls are found by their English text. These are the only
// places tied to UI text; EnglishAppRule pins the app to English.
private const val DESC_SETTINGS = "Settings"
private const val DESC_SEARCH = "Search"
private const val TEXT_SETTINGS = "Settings"
private const val TEXT_THEME = "Theme"
private const val TEXT_AGREE = "I agree"

/**
 * The privacy dialog only shows on a fresh install and, being modal, hides the home screen from
 * accessibility while it is up, so whichever of the two appears first decides.
 */
fun UiDevice.acceptWelcomeIfNeeded() {
    val deadline = SystemClock.uptimeMillis() + UI_TIMEOUT
    while (SystemClock.uptimeMillis() < deadline) {
        val agree = findObject(By.text(TEXT_AGREE))
        if (agree != null) {
            agree.click()
            waitForIdle()
            return
        }
        if (hasObject(By.desc(DESC_SEARCH))) {
            return
        }
        SystemClock.sleep(POLL_INTERVAL)
    }
}

/** The search action only exists once the home screen has been composed, in both the
 *  compact (drawer) and expanded (permanent settings pane) layouts. */
fun UiDevice.waitForHome(): Boolean {
    return wait(Until.hasObject(By.desc(DESC_SEARCH)), UI_TIMEOUT) ?: false
}

/** Returns whether there was a scrollable egg list. */
fun UiDevice.scrollEggList(): Boolean {
    val list = findObject(By.scrollable(true)) ?: return false
    list.setGestureMargin(displayWidth / 5)
    list.fling(Direction.DOWN)
    waitForIdle()
    list.fling(Direction.UP)
    waitForIdle()
    return true
}

/** Returns whether the search bar opened. */
fun UiDevice.searchEggs(query: String): Boolean {
    findObject(By.desc(DESC_SEARCH))?.click()
    val field = wait(Until.findObject(By.clazz("android.widget.EditText")), UI_TIMEOUT)
    field?.text = query
    waitForIdle()
    // The first back dismisses the keyboard, the second closes the search bar.
    pressBack()
    pressBack()
    waitForIdle()
    return field != null
}

/**
 * Settings is a drawer. Its title is a text, while the action that opened it sits behind the
 * scrim, so the title tells whether it opened. Returns whether it did.
 */
fun UiDevice.browseSettings(): Boolean {
    // Compact/medium layouts open settings as a drawer behind an action icon; expanded
    // layouts show it as a permanent pane with no action icon.
    val action = findObject(By.desc(DESC_SETTINGS))
    if (action != null) {
        action.click()
        val opened = wait(Until.hasObject(By.text(TEXT_SETTINGS)), UI_TIMEOUT) ?: false
        findObject(By.scrollable(true))?.fling(Direction.DOWN)
        waitForIdle()
        pressBack()
        waitForIdle()
        return opened
    }
    // The permanent pane has no title, so the first row stands in for it.
    return hasObject(By.text(TEXT_THEME))
}

fun MacrobenchmarkScope.openEgg(apiLevel: Int) {
    startActivityAndWait(eggDeepLink(apiLevel))
    device.waitForIdle()
    device.pressBack()
}
