plugins {
    id("easter.egg.app")
}

android {
    namespace = "com.dede.android_eggs"

    defaultConfig {
        applicationId = "com.dede.android_eggs"
        versionCode = 50
        versionName = "3.1.0-beta01"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resourceConfigurations += listOf(
            "zh", "zh-rTW",
            "ru", "uk-rUA",
            "en", "it", "de", "fr", "nl-rNL", "hu-rHU",
            "es", "pt", "pt-rBR", "pl-rPL", "tr-rTR", "fi-rFI",
            "in-rID", "hr-rHR", /*"la-rLA", */"el-rGR", "no-rNO",
            "ja-rJP", "ko-rKR", "vi-rVN", "th-rTH", "fil-rPH", "lo-rLA",
            "ar-rSA", "cs-rCZ", "ta-rIN", "ro-rRO", "sv-rSE", "my-rMM",
        )

        setProperty("archivesBaseName", "easter_eggs_${versionName}_${versionCode}")

        // Language configuration only
        buildConfigField("int", "LANGUAGE_RES", resourceConfigurations.size.toString())
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

    flavorDimensions += "app"

    productFlavors {
        create("foss") {
            dimension = "app"
        }
        create("market") {
            dimension = "app"
        }
    }

    packaging {
        resources.excludes += listOf(
            "META-INF/*.version",
            "DebugProbesKt.bin"
        )
    }

    dependenciesInfo {
        // https://developer.android.com/build/dependencies#dependency-info-play
        // Disables dependency metadata when building APKs.
        includeInApk = false
    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.livedata)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.window)
    implementation(libs.google.material)
    implementation(libs.androidx.startup)

    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.lifecycle)
    implementation(libs.androidx.compose.viewmodel)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.dionsegijn.konfetti)
    implementation(libs.squareup.okio)
    implementation(libs.free.reflection)
    implementation(libs.blurhash.android)
    debugImplementation(libs.squareup.leakcanary)

    implementation(project(":eggs:AndroidNext"))
    implementation(project(":eggs:VanillaIceCream"))
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
    implementation(project(":eggs:Base"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.nanohttpd)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.android.test)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
