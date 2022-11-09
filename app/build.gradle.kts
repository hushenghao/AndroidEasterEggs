import com.android.build.api.dsl.ManagedVirtualDevice
import java.io.ByteArrayOutputStream
import java.util.*

val keystoreProperties = Properties().apply {
    rootProject.file("key.properties")
        .takeIf { it.exists() }?.inputStream()?.use(this::load)
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Versions.COMPILE_SDK
    buildToolsVersion = Versions.BUILD_TOOLS
    namespace = "com.dede.android_eggs"

    defaultConfig {
        applicationId = "com.dede.android_eggs"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = 22
        versionName = "1.8.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.addAll(listOf("zh", "en"))
        setProperty("archivesBaseName", "easter_eggs_${versionName}_${versionCode}")
    }

    signingConfigs {
        if (keystoreProperties.isEmpty) return@signingConfigs
        create("release") {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        val config = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        getByName("debug") {
            signingConfig = config
        }
        getByName("release") {
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = config
        }
    }

    viewBinding {
        enable = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions {
        animationsDisabled = true

        unitTests {
            isIncludeAndroidResources = true
        }

        managedDevices {
            devices.register<ManagedVirtualDevice>("pixel4Api33") {
                apiLevel = 33
                systemImageSource = "google"
                device = "Pixel 4"
            }
        }
    }
}

dependencies {
    implementation(deps.androidx.appcompat)
    implementation(deps.androidx.core.ktx)
    implementation(deps.androidx.lifecycle.runtime.ktx)
    implementation(deps.androidx.lifecycle.viewmodel.ktx)
    implementation(deps.androidx.preference.ktx)
    implementation(deps.androidx.constraintlayout)
    implementation(deps.google.browser)
    implementation(deps.google.material)
    implementation(deps.free.reflection)
    debugImplementation(deps.leakcanary)
    implementation(project(":basic"))
    implementation(project(":eggs:T"))
    implementation(project(":eggs:S"))
    implementation(project(":eggs:R"))
    implementation(project(":eggs:Q"))
    implementation(project(":eggs:Pie"))
    implementation(project(":eggs:Oreo"))
    implementation(project(":eggs:Nougat"))
    implementation(project(":eggs:Marshmallow"))
    implementation(project(":eggs:Lollipop"))
    implementation(project(":eggs:KitKat"))
    implementation(project(":eggs:Jelly_Bean"))
    implementation(project(":eggs:Ice_Cream_Sandwich"))
    implementation(project(":eggs:Honeycomb"))
    implementation(project(":eggs:Gingerbread"))

    testImplementation(deps.junit)
    androidTestImplementation(deps.bundles.android.test)
    androidTestImplementation(deps.nanohttpd)
}

tasks.register<Exec>("pgyer") {
    val apiKey = keystoreProperties["pgyer.api_key"]
        ?: throw IllegalArgumentException("pgyer.api_key not found")

    val assemble = tasks.named("assembleRelease").get()
    dependsOn("clean", assemble)
    assemble.mustRunAfter("clean")

    val tree = fileTree("build") {
        include("outputs/apk/release/*.apk", "intermediates/apk/release/*.apk")
        builtBy("assembleRelease")
    }
    doFirst {
        val apkPath = tree.single().absolutePath
        println("Upload Apk: $apkPath")

        commandLine(
            "curl", "-F", "file=@$apkPath",
            "-F", "_api_key=$apiKey",
            "-F", "buildUpdateDescription=Upload by gradle pgyer task",
            "https://www.pgyer.com/apiv2/app/upload"
        )
    }
    val output = ByteArrayOutputStream()
    standardOutput = output
    doLast {
        val result = output.toString()
        val obj = org.json.JSONObject(result)
        if (obj.getInt("code") == 0) {
            val path = obj.getJSONObject("data")
                .getString("buildShortcutUrl")
            println("Uploaded successfully: https://www.pgyer.com/$path")
        } else {
            val message = obj.getString("message")
            println("Upload failed: $message")
        }
    }
}
