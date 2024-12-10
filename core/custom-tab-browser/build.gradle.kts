plugins {
    id("easter.egg.library")
}

android {
    namespace = "com.dede.android_eggs.browser"
}

dependencies {
    implementation(project(":core:theme"))
    implementation(project(":core:settings"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.browser)
}
