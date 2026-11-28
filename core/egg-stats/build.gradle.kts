plugins {
    id("easter.eggs.library")
    id("androidx.room3")
}

android {
    namespace = "com.dede.android_eggs.egg_stats"

    room3 {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.startup)
    implementation(libs.androidx.room)
    implementation(libs.androidx.sqlite.framework)
    ksp(libs.androidx.room.compiler)
}
