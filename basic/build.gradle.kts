plugins {
    id("easter.eggs.basic.library")
}

android {
    namespace = "com.dede.basic"
}

dependencies {
    implementation(libs.squareup.okio)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.activity)
}