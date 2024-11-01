plugins {
    id("easter.egg.library")
}

android {
    namespace = "com.dede.android_eggs.embedding_splits"

    resourcePrefix = null

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":theme"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.window)
    implementation(libs.google.material)
    implementation(libs.androidx.startup)

    implementation(libs.androidx.compose.activity)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
