@file:Suppress("UnstableApiUsage")

import Versions.gitHash
import Versions.keyprops
import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

apply(from = "../buildSrc/tasks.gradle.kts")

android {
    compileSdk = Versions.COMPILE_SDK
    buildToolsVersion = Versions.BUILD_TOOLS
    namespace = "com.dede.android_eggs"

    defaultConfig {
        applicationId = "com.dede.android_eggs"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = 31
        versionName = "1.9.8"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations += listOf(
            "zh", "zh-rTW",
            "ru", "uk-rUA",
            "en", "it", "de", "fr", "nl-rNL",
            "es", "pt", "pl-rPL", "tr-rTR",
            "in-rID", "hr-rHR", "la-rLA",
            "ja-rJP", "ko",
            "ar-rSA",
        )

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

    packaging {
        // only coil local image
        resources.excludes += "okhttp3/**"
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    compileOnly(libs.androidx.preference)// Optimize apk size and ensure proper compilation of preference components.
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.window)
    implementation(libs.google.material)
    implementation(libs.androidx.startup)
    implementation(libs.io.coil)
    implementation(libs.free.reflection)
    implementation(libs.viewbinding.delegate)
    implementation(libs.blurhash.android)
    debugImplementation(libs.squareup.leakcanary)
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.nanohttpd)
    androidTestImplementation(libs.bundles.android.test)
}
