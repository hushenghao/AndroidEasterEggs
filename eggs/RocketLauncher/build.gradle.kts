plugins {
    id("easter.egg.library")
}

android {
    namespace = "com.android.launcher2"
}

dependencies {
    implementation(project(":core:resources"))
}
