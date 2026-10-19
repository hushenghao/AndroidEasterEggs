plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.color_picker"
}

dependencies {
    implementation(project(":core:composable"))
    implementation(project(":core:icon-shape"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.activity)

    implementation(libs.androidx.core)
}
