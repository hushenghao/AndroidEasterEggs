package com.dede.android_eggs.baselineprofile

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val ENABLED_RULES = "androidx.benchmark.enabledRules"

/**
 * Black-box smoke test of the minified `benchmarkRelease` build. What only breaks after R8
 * (stripped reflection targets, renamed resources, missing keep rules) shows up here and never
 * in a debug `androidTest`.
 *
 * ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest \
 *     -Pandroid.testInstrumentationRunnerArguments.class=com.dede.android_eggs.baselineprofile.SmokeTest
 */
@RunWith(AndroidJUnit4::class)
class SmokeTest {

    @get:Rule
    val english = EnglishAppRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        // While generating a Baseline Profile the plugin also runs the tests of this module that
        // use no rule; the smoke test only makes sense on the minified build.
        val rules = InstrumentationRegistry.getArguments().getString(ENABLED_RULES).orEmpty()
        assumeFalse(rules.contains("baselineprofile", ignoreCase = true))

        device.wakeUp()
        device.executeShellCommand("am force-stop $PACKAGE_NAME")
        device.startApp()
        device.acceptWelcomeIfNeeded()
    }

    @Test
    fun homeSearchAndSettingsWork() {
        assertTrue("The home screen did not render", device.waitForHome())
        assertTrue("The egg list was not scrollable", device.scrollEggList())
        assertTrue("The search bar did not open", device.searchEggs("android"))
        assertTrue("The settings drawer did not open", device.browseSettings())
        assertNoCrash("home, search and settings")
    }

    @Test
    fun everyEggOpensItsActivity() {
        // An egg newer than the device's own release may not run there.
        EGGS.filter { it.apiLevel <= Build.VERSION.SDK_INT }.forEach { egg ->
            device.clearCrashLog()
            device.startEgg(egg.apiLevel)
            assertEquals(
                "Deep link api/${egg.apiLevel} opened another Activity",
                egg.activity,
                device.awaitResumedActivity(egg.activity),
            )
            assertNoCrash("api/${egg.apiLevel}")
            device.pressBack()
            device.pressBack()
        }
    }

    private fun assertNoCrash(step: String) {
        val log = device.crashLog()
        assertFalse("The app crashed during $step:\n$log", "Process: $PACKAGE_NAME" in log)
    }
}
