plugins {
    id("easter.eggs.basic.library")
}

android {
    namespace = "com.dede.android_eggs.analog_clock"
}

dependencies {
    implementation(project(":core:basic"))
    implementation(project(":core:system-colors"))
    implementation(libs.androidx.core)
}
