plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.alterable_adaptive_icon"
}

dependencies {
    implementation(project(":core:settings"))
    implementation(libs.androidx.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.google.accompanist.drawablepainter)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}