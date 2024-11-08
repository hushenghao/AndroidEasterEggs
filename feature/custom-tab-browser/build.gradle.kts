plugins {
    id("easter.egg.library")
}

android {
    namespace = "com.dede.android_eggs.browser"

    resourcePrefix = null
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.browser)
}
