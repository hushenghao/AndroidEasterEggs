plugins {
    id("easter.eggs.library")
}

android {
    namespace = "com.dede.android_eggs.browser"
}

dependencies {
    implementation(project(":core:theme"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.browser)

    compileOnly(platform(libs.androidx.compose.bom))
    compileOnly(libs.androidx.compose.ui)
}
