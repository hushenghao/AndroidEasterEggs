@file:Suppress("UnstableApiUsage")

plugins {
    id("easter.eggs.benchmark")
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.dede.android_eggs.baselineprofile"

    defaultConfig {
        // The producer needs one flavor only: profile rules are class/method based, so the
        // foss profile also covers the market variant.
        missingDimensionStrategy("app", "foss")
        missingDimensionStrategy("track", "product")
    }

    targetProjectPath = ":app"

    testOptions.managedDevices.localDevices {
        create("pixel6Api33") {
            device = "Pixel 6"
            apiLevel = 33
            // Below API 33 generating a profile needs root, which only "aosp" images offer.
            systemImageSource = "aosp"
        }
    }
}

// Generate on the attached device instead of the managed one:
// ./gradlew :app:generateBaselineProfile -Peggs.baselineProfile.connected=true
val useConnectedDevice = providers.gradleProperty("eggs.baselineProfile.connected")
    .map(String::toBoolean)
    .getOrElse(false)

baselineProfile {
    if (!useConnectedDevice) {
        managedDevices += "pixel6Api33"
    }
    useConnectedDevices = useConnectedDevice
}

dependencies {
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.junit)
}
