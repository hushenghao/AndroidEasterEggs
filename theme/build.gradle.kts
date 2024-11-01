plugins {
    id("easter.egg.library")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.dede.android_eggs.views.theme"

    resourcePrefix = null

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.google.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
