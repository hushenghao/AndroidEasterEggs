plugins {
    id("easter.eggs.library")
}

android {
    namespace = "com.android.launcher2"
}

dependencies {
    implementation(project(":core:resources"))
}
