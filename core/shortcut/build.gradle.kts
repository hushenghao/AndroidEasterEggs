plugins {
    id("easter.eggs.library")
}

android {
    namespace = "com.dede.android_eggs.shortcut"
}

dependencies {
    implementation(project(":core:alterable-adaptive-icon"))
    implementation(project(":core:resources"))
    implementation(libs.androidx.core)
}