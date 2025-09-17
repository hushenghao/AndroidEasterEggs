plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.crash"
}

dependencies {
    implementation(project(":core:theme"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.startup)

    implementation(libs.androidx.compose.activity)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.squareup.curtains)
}
