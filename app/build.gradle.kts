@file:Suppress("UnstableApiUsage")

import com.dede.android_eggs.dls.marketImplementation

plugins {
    id("easter.eggs.app")
    alias(libs.plugins.aboutlibraries.android)
}

android {
    namespace = "com.dede.android_eggs"

    androidResources {
        localeFilters += listOf(
            "zh", "zh-rTW",
            "ru", "uk-rUA",
            "en", "it", "de", "fr", "nl-rNL", "hu-rHU",
            "es", "pt", "pt-rBR", "pl-rPL", "tr-rTR", "fi-rFI",
            "in-rID", "hr-rHR", /*"la-rLA", */"el-rGR", "no-rNO",
            "ja-rJP", "ko-rKR", "vi-rVN", "th-rTH", "fil-rPH", "lo-rLA",
            "ar-rSA", "cs-rCZ", "ta-rIN", "ro-rRO", "sv-rSE", "my-rMM",
            "bn-rBD",
        )
    }

    defaultConfig {
        applicationId = "com.dede.android_eggs"
        versionCode = 69
        versionName = "4.3.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        base.archivesName.set("easter_eggs_${versionName}_${versionCode}")

        // Language configuration only
        buildConfigField("int", "LANGUAGE_RES", androidResources.localeFilters.size.toString())
    }

    flavorDimensions += listOf("app", "track")

    productFlavors {
        create("foss") {
            dimension = "app"
        }
        create("market") {
            dimension = "app"
        }

        create("alpha") {
            dimension = "track"
            versionNameSuffix = "-alpha"
        }
        create("beta") {
            dimension = "track"
            versionNameSuffix = "-beta01"
        }
        create("product") {
            dimension = "track"
        }
    }

    androidComponents {
        beforeVariants { variantBuilder ->
            val app = variantBuilder.productFlavors[0].second
            val track = variantBuilder.productFlavors[1].second
            if (app == "market") {
                // Enable product only for market build
                variantBuilder.enable = track == "product"
            }
        }
    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.livedata)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.lifecycle)
    implementation(libs.androidx.compose.viewmodel)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.hilt.navigation)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.google.accompanist.drawablepainter)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.dionsegijn.konfetti)
    implementation(libs.squareup.okio)
    implementation(libs.blurhash.android)
    debugImplementation(libs.squareup.leakcanary)

    implementation(project(":core:local-provider"))
    implementation(project(":core:navigation"))
    implementation(project(":core:theme"))
    implementation(project(":core:icons"))
    implementation(project(":core:settings"))
    implementation(project(":core:composable"))
    implementation(project(":core:shortcut"))
    implementation(project(":core:activity-actions"))
    implementation(project(":core:resources"))
    implementation(project(":core:alterable-adaptive-icon"))
    implementation(project(":core:custom-tab-browser"))

    implementation(project(":feature:cat-editor"))
    implementation(project(":feature:widget"))
    implementation(project(":feature:crash"))
    implementation(project(":feature:embedding-splits"))
    implementation(project(":feature:libraries-info"))

    implementation(project(":eggs:RocketLauncher"))
    implementation(project(":eggs:AndroidNext"))
    implementation(project(":eggs:Baklava"))
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

    marketImplementation(libs.google.play.review)

    testImplementation(libs.junit)
    androidTestImplementation(libs.nanohttpd)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.android.test)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
