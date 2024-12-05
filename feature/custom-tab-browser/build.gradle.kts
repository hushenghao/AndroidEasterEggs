plugins {
    id("easter.egg.library")
}

android {
    namespace = "com.dede.android_eggs.browser"

    resourcePrefix = null
}

dependencies {
    implementation(project(":theme"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.browser)
}
