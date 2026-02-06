plugins {
    id("easter.eggs.compose.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dede.android_eggs.navigation"
}

dependencies {
    implementation(project(":core:local-provider"))

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.compose.material3)
}