plugins {
    id("easter.egg.compose.library")
}

android {
    namespace = "com.dede.android_eggs.cat_editor"
}

dependencies {
    implementation(project(":core:theme"))
    implementation(project(":core:resources"))

    implementation(libs.androidx.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.shreyaspatil.capturable)
}
