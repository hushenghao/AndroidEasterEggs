plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.icon_shape"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.shapes)
    implementation(libs.squircle.shape)
}
