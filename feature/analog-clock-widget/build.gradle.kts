plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.dede.android_eggs.views.widget"
}

dependencies {
    implementation(project(":core:theme"))
    implementation(project(":core:resources"))
    implementation(project(":core:settings"))
    implementation(project(":core:icons"))
    implementation(libs.androidx.core)
    implementation(libs.androidx.datastore)
    implementation(libs.google.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.google.accompanist.drawablepainter)
}
