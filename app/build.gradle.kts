import java.util.*

val keystoreProperties = Properties().apply {
    rootProject.file("key.properties")
        .takeIf { it.exists() }?.inputStream()?.use(this::load)
}

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = Versions.COMPILE_SDK
    buildToolsVersion = Versions.BUILD_TOOLS

    defaultConfig {
        applicationId = "com.dede.android_eggs"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = 5
        versionName = "1.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.add("en")
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
        isEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}")
    implementation("androidx.core:core-ktx:${Versions.CORE_KTX}")
    implementation("androidx.appcompat:appcompat:${Versions.APPCOMPAT}")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.browser:browser:1.3.0")
    implementation("com.google.androidbrowserhelper:androidbrowserhelper:2.2.0")
    implementation(project(":basic"))
    implementation(project(":eggs:S_beta"))
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

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

val pgyer = tasks.create<Exec>("pgyer") {
    val apiKey = keystoreProperties["pgyer.api_key"]
    commandLine(
        "curl", "-F",
        "-F", "_api_key=$apiKey",
        "-F", "buildUpdateDescription=Upload by gradle pgyer task",
        "https://www.pgyer.com/apiv2/app/upload"
    )
    doLast {
        if (apiKey == null) {
            throw IllegalArgumentException("pgyer.api_key undefind")
        }
        println("\nUpload Completed!")
    }
}

afterEvaluate {
    val assemble = tasks.findByName("assembleRelease") as Task
    pgyer.dependsOn("clean", assemble)
    assemble.mustRunAfter("clean")
//    pgyer.dependsOn(assemble)
    assemble.doLast {
        val tree = fileTree("build/outputs/apk/release") {
            include("*.apk")
        }
        val apkFile = tree.singleFile
        pgyer.commandLine = pgyer.commandLine.apply {
            add(2, "file=@${apkFile.absolutePath}")
        }
    }
}
