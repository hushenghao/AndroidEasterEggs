plugins {
    id("easter.egg.library")
}

android {
    namespace = "com.dede.android_eggs.alterable_adaptive_icon"
}

dependencies {
    implementation(project(":core:settings"))
    implementation(libs.androidx.core)
}