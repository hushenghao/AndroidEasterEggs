plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.local_provider"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
}
