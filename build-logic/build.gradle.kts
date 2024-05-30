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
        register("easterEggBasicLibrary") {
            id = "easter.egg.basic.library"
            implementationClass = "EasterEggBasicLibrary"
        }
        register("easterEggLibrary") {
            id = "easter.egg.library"
            implementationClass = "EasterEggLibrary"
        }
        register("easterEggApp") {
            id = "easter.egg.app"
            implementationClass = "EasterEggApp"
        }
    }
}