plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.settings_state"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
}
