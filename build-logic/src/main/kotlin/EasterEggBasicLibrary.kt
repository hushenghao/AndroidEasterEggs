import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

open class EasterEggBasicLibrary : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
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
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }
        }
    }
}