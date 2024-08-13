plugins {
    id("easter.egg.basic.library")
}

android {
    namespace = "com.dede.basic"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.startup)
    implementation(libs.google.material)
    implementation(libs.io.coil)
}