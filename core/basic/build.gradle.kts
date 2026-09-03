plugins {
    id("easter.eggs.basic.library")
}

android {
    namespace = "com.dede.basic"
    lint {
        // False positive: the Initializer meta-data is declared in the library manifest,
        // but the check cannot match the inner class name GlobalContext$Initializer.
        disable += "EnsureInitializerMetadata"
    }
}

dependencies {
    api(project(":core:provider"))
    implementation(libs.squareup.okio)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.activity)
}