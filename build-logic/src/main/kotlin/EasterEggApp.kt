import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class EasterEggApp : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.android.application")
                apply("com.google.dagger.hilt.android")
                apply("org.jetbrains.kotlin.android")
                apply("kotlin-kapt")
            }

            dependencies {
                "implementation"(project(path = ":basic"))
                "implementation"(catalog.findLibrary("hilt.android").get())
                "kapt"(catalog.findLibrary("hilt.compiler").get())
            }

            configureAndroid<BaseAppModuleExtension> {
                defaultConfig {
                    targetSdk = Versions.TARGET_SDK
                }

                buildFeatures {
                    buildConfig = true
                }

                lint {
                    fatal += listOf("NewApi", "InlineApi")
                }
            }
        }
    }
}