@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ManagedVirtualDevice
import java.util.*

val keystoreProperties = Properties().apply {
    rootProject.file("key.properties")
        .takeIf { it.exists() }?.inputStream()?.use(this::load)
}

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
        versionCode = 26
        versionName = "1.9.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.addAll(listOf("zh", "en"))
        setProperty("archivesBaseName", "easter_eggs_${versionName}_${versionCode}")
    }

    signingConfigs {
        if (keystoreProperties.isEmpty) return@signingConfigs
        create("release") {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation(deps.androidx.core.ktx)
    implementation(deps.androidx.lifecycle.runtime.ktx)
    implementation(deps.androidx.lifecycle.viewmodel.ktx)
    implementation(deps.androidx.preference.ktx)
    implementation(deps.androidx.constraintlayout)
    implementation(deps.androidx.browser)
    implementation(deps.google.material)
    implementation(deps.io.coil)
    implementation(deps.free.reflection)
    debugImplementation(deps.leakcanary)
    implementation(project(":basic"))
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
    implementation(project(":eggs:Jelly_Bean"))
    implementation(project(":eggs:Ice_Cream_Sandwich"))
    implementation(project(":eggs:Honeycomb"))
    implementation(project(":eggs:Gingerbread"))

    testImplementation(deps.junit)
    androidTestImplementation(deps.nanohttpd)
    androidTestImplementation(deps.bundles.android.test)
}
