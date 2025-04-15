plugins {
    id("easter.egg.compose.library")
}

android {
    namespace = "com.dede.android_eggs.ui.composes.icons"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
}
