package com.dede.android_eggs.baselineprofile

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.ExternalResource

/**
 * The journeys find controls by their English text, so pin the app (not the device) to English
 * and put back whatever was set before. `cmd locale` exists from API 33; below that the app
 * follows the device language and the journeys need an English device.
 */
class EnglishAppRule : ExternalResource() {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private var previous: String? = null

    override fun before() {
        val current = device.executeShellCommand("cmd locale get-app-locales $PACKAGE_NAME")
        previous = APP_LOCALES.find(current)?.groupValues?.get(1)?.replace(" ", "")
        setAppLocales("en")
    }

    override fun after() {
        previous?.let { setAppLocales(it) }
    }

    private fun setAppLocales(locales: String) {
        val flag = if (locales.isEmpty()) "" else " --locales $locales"
        device.executeShellCommand("cmd locale set-app-locales $PACKAGE_NAME$flag")
    }

    private companion object {
        // "Locales for com.dede.android_eggs for user 0 are [en]"
        val APP_LOCALES = Regex("""\[(.*)]""")
    }
}
