plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.composable"
}

dependencies {
    implementation(project(":basic"))
    implementation(project(":core:theme"))
    implementation(project(":core:settings"))
    implementation(libs.google.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
}
