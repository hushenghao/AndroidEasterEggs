plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.settings_ui"
}

dependencies {
    implementation(project(":core:icon-shape"))

    implementation(libs.androidx.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
}
