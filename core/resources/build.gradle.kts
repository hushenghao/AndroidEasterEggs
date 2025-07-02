plugins {
    id("easter.eggs.library")
}

android {
    namespace = "com.dede.android_eggs.resources"
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
}