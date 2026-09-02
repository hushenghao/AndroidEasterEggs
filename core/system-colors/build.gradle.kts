plugins {
    id("easter.eggs.basic.library")
}

android {
    namespace = "com.dede.android_eggs.system_colors"
    lint {
        // False positive: the Initializer meta-data is declared in the library manifest,
        // but the check cannot match the inner class name WallpaperTonalColors$Initializer.
        disable += "EnsureInitializerMetadata"
    }
}

dependencies {
    implementation(project(":core:basic"))
    implementation(libs.material.color.utilities)
    implementation(libs.androidx.core)
    implementation(libs.androidx.startup)
}
