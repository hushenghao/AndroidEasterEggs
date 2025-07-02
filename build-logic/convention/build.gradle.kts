plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.android.tools.sdk.common)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("basic") {
            id = "easter.eggs.basic.library"
            implementationClass = "com.dede.android_eggs.plugins.EasterEggsBasicLibrary"
        }
        register("library") {
            id = "easter.eggs.library"
            implementationClass = "com.dede.android_eggs.plugins.EasterEggsLibrary"
        }
        register("composeLibrary") {
            id = "easter.eggs.compose.library"
            implementationClass = "com.dede.android_eggs.plugins.EasterEggsComposeLibrary"
        }
        register("app") {
            id = "easter.eggs.app"
            implementationClass = "com.dede.android_eggs.plugins.EasterEggsApp"
        }
    }
}