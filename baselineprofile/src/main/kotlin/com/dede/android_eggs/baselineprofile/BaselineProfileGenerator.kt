package com.dede.android_eggs.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule(order = 0)
    val english = EnglishAppRule()

    @get:Rule(order = 1)
    val rule = BaselineProfileRule()

    // Cold start only: this is what the startup profile (dex layout optimization) is built from.
    @Test
    fun startup() = rule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()
        device.acceptWelcomeIfNeeded()
        device.waitForHome()
    }

    @Test
    fun browse() = rule.collect(packageName = PACKAGE_NAME) {
        pressHome()
        startActivityAndWait()
        device.acceptWelcomeIfNeeded()
        device.waitForHome()
        device.scrollEggList()
        device.searchEggs("android")
        device.browseSettings()
    }

    @Test
    fun eggs() = rule.collect(packageName = PACKAGE_NAME) {
        pressHome()
        startActivityAndWait()
        device.acceptWelcomeIfNeeded()
        device.waitForHome()
        PROFILED_EGG_LEVELS.forEach { openEgg(it) }
    }

    private companion object {
        val PROFILED_EGG_LEVELS = listOf(31, 33, 34, 35, 36)
    }
}
