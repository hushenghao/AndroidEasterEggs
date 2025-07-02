plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.views.theme"
}

dependencies {
    implementation(project(":core:settings"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.google.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
