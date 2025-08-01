plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.composable"
}

dependencies {
    implementation(project(":core:theme"))
    implementation(project(":core:settings"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
