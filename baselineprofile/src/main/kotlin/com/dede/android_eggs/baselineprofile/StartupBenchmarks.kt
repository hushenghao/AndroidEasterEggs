package com.dede.android_eggs.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compares a cold start without any ahead-of-time compilation against one with the Baseline
 * Profile installed. Run it on the `benchmarkRelease` variant on a physical device:
 *
 * ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest \
 *     -Pandroid.testInstrumentationRunnerArguments.class=com.dede.android_eggs.baselineprofile.StartupBenchmarks
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmarks {

    @get:Rule(order = 0)
    val english = EnglishAppRule()

    @get:Rule(order = 1)
    val rule = MacrobenchmarkRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // On a fresh install the privacy dialog would cover the first measured launch only.
    @Before
    fun acceptWelcome() {
        device.startApp()
        device.acceptWelcomeIfNeeded()
        device.pressHome()
    }

    @Test
    fun startupCompilationNone() = benchmark(CompilationMode.None())

    @Test
    fun startupCompilationBaselineProfiles() =
        benchmark(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun benchmark(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 10,
            setupBlock = {
                pressHome()
            },
        ) {
            startActivityAndWait()
        }
    }
}
