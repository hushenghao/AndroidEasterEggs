plugins {
    id("easter.eggs.compose.library")
}

android {
    namespace = "com.android_baklava.egg"
    resourcePrefix = "baklava_"

    lint {
        baseline = project.file("lint-baseline.xml")
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.window)

    implementation(libs.androidx.compose.activity)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
