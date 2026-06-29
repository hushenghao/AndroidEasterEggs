plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.android_b.egg"
    resourcePrefix = "b_"
}

dependencies {
    implementation(project(":core:theme"))
    implementation(libs.androidx.core)

    implementation(libs.androidx.compose.activity)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.google.accompanist.drawablepainter)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
