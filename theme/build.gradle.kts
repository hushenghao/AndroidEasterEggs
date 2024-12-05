plugins {
    id("easter.egg.compose.library")
}

android {
    namespace = "com.dede.android_eggs.views.theme"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
