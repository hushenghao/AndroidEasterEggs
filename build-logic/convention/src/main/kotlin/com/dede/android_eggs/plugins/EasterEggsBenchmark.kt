package com.dede.android_eggs.plugins

import Versions
import com.android.build.api.dsl.TestExtension
import com.dede.android_eggs.dls.android
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Self-instrumenting `com.android.test` module that drives the app from the outside: Baseline
 * Profile generation, Macrobenchmark and the minified release smoke test.
 *
 * Deliberately not an [AbsConfigurablePlugin]: it has no code of its own that needs Hilt,
 * Compose, `:core:basic` or core library desugaring.
 */
class EasterEggsBenchmark : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.test")

            android<TestExtension> {
                compileSdk(Versions.compileSdk)
                buildToolsVersion = Versions.BUILD_TOOLS

                with(defaultConfig) {
                    // Generating a Baseline Profile needs a rooted API 28+ device or any API 33+ one.
                    minSdk = MIN_SDK
                    targetSdk(Versions.targetSdk)
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                experimentalProperties["android.experimental.self-instrumenting"] = true
            }
        }
    }

    private companion object {
        const val MIN_SDK = 28
    }
}
