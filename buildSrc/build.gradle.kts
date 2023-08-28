plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    implementation(gradleApi())
    implementation("com.android.tools:sdk-common:31.1.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
}
