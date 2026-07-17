plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.keep_android_open"
}

dependencies {
    implementation(project(":core:settings"))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
