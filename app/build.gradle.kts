@file:Suppress("UnstableApiUsage")

import Versions.gitHash
import Versions.keyprops
import com.android.build.api.dsl.ManagedVirtualDevice
import java.util.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Versions.COMPILE_SDK
    buildToolsVersion = Versions.BUILD_TOOLS
    namespace = "com.dede.android_eggs"

    defaultConfig {
        applicationId = "com.dede.android_eggs"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = 29
        versionName = "1.9.6"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations.addAll(listOf("zh", "zh-rHK", "en"))

        setProperty("archivesBaseName", "easter_eggs_${versionName}_${versionCode}")
        buildConfigField("String", "GIT_HASH", "\"${gitHash}\"")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

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
        val config = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        getByName("debug") {
            signingConfig = config
        }
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = config
        }
    }

    viewBinding {
        enable = true
    }

    lint {
        disable += listOf("NotifyDataSetChanged")
        fatal += listOf("NewApi", "InlineApi")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isIncludeAndroidResources = true
        }

        managedDevices {
            devices.register<ManagedVirtualDevice>("pixel4Api33") {
                apiLevel = 33
                systemImageSource = "google"
                device = "Pixel 4"
            }
        }
    }
}

dependencies {
    implementation(deps.androidx.appcompat)
    implementation(deps.androidx.core)
    implementation(deps.androidx.activity)
    implementation(deps.androidx.lifecycle.runtime)
    implementation(deps.androidx.lifecycle.viewmodel)
    compileOnly(deps.androidx.preference)// Optimize apk size and ensure proper compilation of preference components.
    implementation(deps.androidx.constraintlayout)
    implementation(deps.androidx.browser)
    implementation(deps.androidx.window)
    implementation(deps.google.material)
    implementation(deps.androidx.startup)
    implementation(deps.io.coil)
    implementation(deps.free.reflection)
    implementation(deps.viewbinding.delegate)
    implementation(deps.blurhash.android)
    debugImplementation(deps.squareup.leakcanary)
    implementation(project(":basic"))
    implementation(project(":eggs:U"))
    implementation(project(":eggs:T"))
    implementation(project(":eggs:S"))
    implementation(project(":eggs:R"))
    implementation(project(":eggs:Q"))
    implementation(project(":eggs:Pie"))
    implementation(project(":eggs:Oreo"))
    implementation(project(":eggs:Nougat"))
    implementation(project(":eggs:Marshmallow"))
    implementation(project(":eggs:Lollipop"))
    implementation(project(":eggs:KitKat"))
    implementation(project(":eggs:JellyBean"))
    implementation(project(":eggs:IceCreamSandwich"))
    implementation(project(":eggs:Honeycomb"))
    implementation(project(":eggs:Gingerbread"))

    testImplementation(deps.junit)
    testImplementation(deps.android.tools.sdk.common)
    testImplementation(deps.squareup.okhttp)
    androidTestImplementation(deps.nanohttpd)
    androidTestImplementation(deps.bundles.android.test)
}
