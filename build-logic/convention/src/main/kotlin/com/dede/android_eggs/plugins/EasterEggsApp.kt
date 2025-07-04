package com.dede.android_eggs.plugins

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.dede.android_eggs.dls.android
import com.dede.android_eggs.dls.keyprops
import com.dede.android_eggs.tasks.UpdateChangelogsTask
import org.gradle.api.Project

class EasterEggsApp : AbsConfigurablePlugin(
    moduleType = ModuleType.APP,
    isHiltEnable = true,
    isComposeEnabled = true
) {

    override fun Project.onApply() {
        android<BaseAppModuleExtension> {

            signingConfigs {
                if (keyprops.isEmpty) return@signingConfigs
                create("release") {
                    keyAlias = keyprops.getProperty("keyAlias")
                    keyPassword = keyprops.getProperty("keyPassword")
                    storeFile = file(keyprops.getProperty("storeFile"))
                    storePassword = keyprops.getProperty("storePassword")
                    enableV3Signing = true
                    enableV4Signing = true
                }
            }

            buildTypes {
                val config =
                    signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
                debug {
                    signingConfig = config
                    // vcsInfo.include = true
                }
                release {
                    isShrinkResources = true
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                    signingConfig = config
                }
            }

            packaging {
                resources.excludes += listOf(
                    "META-INF/*.version",
                    "META-INF/NOTICE.*",
                    "META-INF/LICENSE",
                    "META-INF/**/LICENSE.txt",
                    "kotlin/**.kotlin_builtins",
                    "DebugProbesKt.bin",
                    "*.properties"
                )
            }

            dependenciesInfo {
                // https://developer.android.com/build/dependencies#dependency-info-play
                // Disables dependency metadata when building APKs.
                includeInApk = false
            }
        }

        registerTasks()
    }

    private fun Project.registerTasks() {
        UpdateChangelogsTask.register(this)
    }
}
