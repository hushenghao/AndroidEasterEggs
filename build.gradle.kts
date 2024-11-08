plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
