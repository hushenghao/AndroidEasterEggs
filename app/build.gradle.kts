import Versions.gitHash
import Versions.keyprops

plugins {
    id("easter.egg.app")
}

android {
    namespace = "com.dede.android_eggs"

    defaultConfig {
        applicationId = "com.dede.android_eggs"
        versionCode = 38
        versionName = "2.3.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations += listOf(
            "zh", "zh-rTW",
            "ru", "uk-rUA",
            "en", "it", "de", "fr", "nl-rNL", "hu-rHU",
            "es", "pt", "pt-rBR", "pl-rPL", "tr-rTR", "fi-rFI",
            "in-rID", "hr-rHR", /*"la-rLA", */"el-rGR", "no-rNO",
            "ja-rJP", "ko", "vi-rVN", "th-rTH", "fil-rPH", "lo-rLA",
            "ar-rSA", "cs-rCZ", "ta-rIN", "ro-rRO",
        )

        setProperty("archivesBaseName", "easter_eggs_${versionName}_${versionCode}")

        buildConfigField("String", "GIT_HASH", "\"${gitHash}\"")
        // Language configuration only
        buildConfigField("int", "LANGUAGE_RES", resourceConfigurations.size.toString())
    }

//    kotlinOptions {
//        freeCompilerArgs += listOf(
//            "-P",
//            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
//        )
//    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.kotlin.compiler.get()
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
        debug {
            signingConfig = config
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

    viewBinding {
        enable = true
    }

    lint {
        disable += listOf("NotifyDataSetChanged", "UsingMaterialAndMaterial3Libraries")
    }

    packaging {
        resources.excludes += listOf(
            "okhttp3/**",// only coil local image
            "META-INF/*.version"
        )
    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.window)
    implementation(libs.google.material)
    implementation(libs.androidx.startup)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
//    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.accompanist.drawablepainter)

    implementation(libs.dionsegijn.konfetti)
    implementation(libs.io.coil)
//    implementation(libs.io.coil.compose)
    implementation(libs.free.reflection)
    implementation(libs.viewbinding.delegate)
    implementation(libs.blurhash.android)
//    implementation(libs.blurhash.painter)
    debugImplementation(libs.squareup.leakcanary)
    implementation(libs.overscroll.decor.android)

    implementation(project(":eggs:UpsideDownCake"))
    implementation(project(":eggs:Tiramisu"))
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
