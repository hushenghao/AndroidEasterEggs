plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.neko_controls_widget"
}

dependencies {
    implementation(project(":core:settings"))
    implementation(project(":eggs:R"))

    implementation(libs.androidx.core)
    implementation(libs.google.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
