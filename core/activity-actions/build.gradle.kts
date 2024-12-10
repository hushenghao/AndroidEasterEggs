plugins {
    id("easter.egg.compose.library")
}

android {
    namespace = "com.dede.android_eggs.activity_actions"
}

dependencies {
    implementation(project(":core:theme"))
    implementation(project(":core:resources"))
    implementation(project(":core:shortcut"))

    // cats
    implementation(project(":eggs:Tiramisu"))
    implementation(project(":eggs:S"))
    implementation(project(":eggs:R"))
    implementation(project(":eggs:Nougat"))

    implementation(libs.androidx.startup)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
}