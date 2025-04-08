plugins {
    id("easter.egg.compose.library")
}

android {
    namespace = "com.dede.android_eggs.navigation"
}

dependencies {
    implementation(project(":core:local-provider"))

    implementation(libs.androidx.compose.navigation)
}