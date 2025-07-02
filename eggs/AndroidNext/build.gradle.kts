plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.android_next.egg"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.google.accompanist.drawablepainter)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(project(":core:custom-tab-browser"))
    implementation(project(":core:navigation"))
}
