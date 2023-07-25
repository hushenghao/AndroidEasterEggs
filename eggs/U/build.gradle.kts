plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Versions.COMPILE_SDK
    buildToolsVersion = Versions.BUILD_TOOLS
    defaultConfig {
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK

        consumerProguardFiles("consumer-rules.pro")

        vectorDrawables {
            useSupportLibrary = false
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
//        freeCompilerArgs += listOf(
//            "-P",
//            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
//        )
    }
//    buildFeatures {
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.4.6"
//    }
}

dependencies {
    implementation(deps.androidx.appcompat)
    implementation(deps.androidx.core)
    implementation(deps.androidx.window)
    implementation(project(path = ":basic"))

//    implementation("androidx.activity:activity-compose:1.7.0")
//    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
//    implementation("androidx.compose.ui:ui")
//    implementation("androidx.compose.ui:ui-graphics")
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.compose.material3:material3")
}