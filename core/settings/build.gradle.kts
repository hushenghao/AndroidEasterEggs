plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.settings"
}

dependencies {
    implementation(project(":core:theme"))

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.shapes)
    implementation(libs.squircle.shape)
}
