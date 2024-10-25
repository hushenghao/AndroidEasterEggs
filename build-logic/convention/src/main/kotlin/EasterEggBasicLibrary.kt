import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

open class EasterEggBasicLibrary : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            dependencies {
                "implementation"(project(path = ":jvm-basic"))
            }

            configureAndroid<LibraryExtension> {
                defaultConfig {
                    consumerProguardFiles("consumer-rules.pro")
                }

                lint {
                    fatal += listOf("NewApi", "InlinedApi")
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                buildFeatures {
                    buildConfig = false
                }
            }
        }
    }
}