plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.navigation"
}

dependencies {
    implementation(project(":core:local-provider"))

    implementation(libs.androidx.compose.navigation)
}