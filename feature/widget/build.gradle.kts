plugins {
    id("easter.egg.library")
}

android {
    namespace = "com.dede.android_eggs.views.widget"

    resourcePrefix = null
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.google.material)
}
