package com.dede.android_eggs.plugins

import Versions
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.dede.android_eggs.dls.android
import com.dede.android_eggs.dls.javaExtension
import com.dede.android_eggs.dls.library
import com.dede.android_eggs.dls.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

abstract class AbsConfigurablePlugin(
    protected val configurable: Configurable
) : Plugin<Project> {

    constructor(
        moduleType: ModuleType,
        isHiltEnable: Boolean = false,
        isComposeEnabled: Boolean = false,
        isBaselineEnabled: Boolean = false,
    ) : this(
        Configurable(
            moduleType = moduleType,
            isHiltEnable = isHiltEnable,
            isComposeEnabled = isComposeEnabled,
            isBaselineEnabled = isBaselineEnabled,
        )
    )

    enum class ModuleType {
        APP,
        LIBRARY,
        BASIC,
    }

    data class Configurable(
        val moduleType: ModuleType,

        val isHiltEnable: Boolean = false,
        val isComposeEnabled: Boolean = false,

        val isBaselineEnabled: Boolean = false,
    )

    final override fun apply(target: Project) {
        with(target) {
            applyPlugins(configurable)

            setupToolchains(configurable)

            configureAndroid(configurable)

            addDependencies(configurable)

            onApply()
        }
    }

    protected open fun Project.onApply() {}

    private fun Project.applyPlugins(configurable: Configurable) {
        with(pluginManager) {
            when (configurable.moduleType) {
                ModuleType.APP -> {
                    apply("com.android.application")
                }
                ModuleType.LIBRARY,
                ModuleType.BASIC -> {
                    apply("com.android.library")
                }
            }
            apply("org.jetbrains.kotlin.android")
            if (configurable.isHiltEnable) {
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
            }
            if (configurable.isComposeEnabled) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun Project.setupToolchains(configurable: Configurable) {
        javaExtension.toolchain {
            languageVersion.set(Versions.JAVA_VERSION)
        }

        kotlinExtension.jvmToolchain {
            languageVersion.set(Versions.JAVA_VERSION)
        }

        extensions.configure<KotlinAndroidProjectExtension>("kotlin") {
            compilerOptions {
                // https://kotlinlang.org/docs/annotations.html#annotation-use-site-targets
                // https://youtrack.jetbrains.com/issue/KT-73255
                freeCompilerArgs.addAll("-Xannotation-default-target=param-property")
            }
        }
    }

    private fun Project.configureAndroid(configurable: Configurable) {

        android<CommonExtension<*, *, *, *, *, *>> {
            compileSdk = Versions.COMPILE_SDK
            buildToolsVersion = Versions.BUILD_TOOLS

            defaultConfig {
                minSdk = Versions.MIN_SDK

                vectorDrawables {
                    useSupportLibrary = true
                }
            }

            buildFeatures {
                buildConfig = configurable.moduleType == ModuleType.APP
                compose = configurable.isComposeEnabled
            }

            when (configurable.moduleType) {
                ModuleType.APP -> {
                    with(this as AppExtension) {
                        defaultConfig {
                            targetSdk = Versions.TARGET_SDK
                        }
                    }
                }
                ModuleType.LIBRARY,
                ModuleType.BASIC -> {
                    with(this as LibraryExtension) {
                        defaultConfig {
                            consumerProguardFiles("consumer-rules.pro")
                        }

                        buildTypes {
                            release {
                                isMinifyEnabled = false
                                proguardFiles(
                                    getDefaultProguardFile("proguard-android-optimize.txt"),
                                    "proguard-rules.pro"
                                )
                            }
                        }
                    }
                }
            }

            lint {
                fatal += listOf("NewApi", "InlinedApi")
                if (configurable.isBaselineEnabled) {
                    baseline = project.file("lint-baseline.xml")
                }
            }




        }
    }

    private fun Project.addDependencies(configurable: Configurable) {
        dependencies {
            when (configurable.moduleType) {
                ModuleType.APP,
                ModuleType.LIBRARY -> {
                    "implementation"(project(":basic"))
                }
                ModuleType.BASIC -> {
                    "api"(project(":jvm-basic"))
                }
            }
            if (configurable.isHiltEnable) {
                "implementation"(libs.library("hilt.android"))
                "ksp"(libs.library("hilt.compiler"))
            }
            if (configurable.isComposeEnabled) {
                "implementation"(platform(libs.library("androidx.compose.bom")))
                "implementation"("androidx.compose.foundation:foundation")
                "implementation"("androidx.compose.ui:ui")
            }
        }
    }
}
