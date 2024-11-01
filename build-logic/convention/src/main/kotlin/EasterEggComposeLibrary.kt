import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class EasterEggComposeLibrary : EasterEggBasicLibrary() {

    override fun apply(target: Project) {
        super.apply(target)
        with(target) {
            pluginManager.apply {
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            dependencies {
                "implementation"(project(path = ":basic"))
                "implementation"(catalog.findLibrary("hilt.android").get())
                "ksp"(catalog.findLibrary("hilt.compiler").get())
            }

            configureAndroid<LibraryExtension> {
                buildFeatures {
                    buildConfig = false
                    compose = true
                }
            }
        }
    }

}