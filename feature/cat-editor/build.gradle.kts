plugins {
    id("easter.eggs.compose.library")
    id("androidx.room")
}

android {
    namespace = "com.dede.android_eggs.cat_editor"

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(project(":core:theme"))
    implementation(project(":core:resources"))
    implementation(project(":core:local-provider"))
    implementation(project(":core:navigation"))
    implementation(project(":core:icons"))

    implementation(libs.androidx.core)
    implementation(libs.google.material)
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.compose.navigation)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.google.accompanist.permissions)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.shreyaspatil.capturable)
}
