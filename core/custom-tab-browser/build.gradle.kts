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
}
