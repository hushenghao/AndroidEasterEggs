plugins {
    id("easter.eggs.library")
}

android {
    namespace = "com.dede.android_eggs.views.widget"
}

dependencies {
    implementation(project(":core:resources"))
    implementation(libs.androidx.core)
    implementation(libs.google.material)
}
