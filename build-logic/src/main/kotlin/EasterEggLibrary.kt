import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

class EasterEggLibrary : EasterEggBasicLibrary() {

    override fun apply(target: Project) {
        super.apply(target)
        with(target) {
            pluginManager.apply {
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
            }

            dependencies {
                "implementation"(project(path = ":basic"))
                "implementation"(catalog.findLibrary("hilt.android").get())
                "ksp"(catalog.findLibrary("hilt.compiler").get())
            }

            configureAndroid<LibraryExtension> {
                val key = name.substring(0, 1).lowercase()
                namespace = "com.android_$key.egg"
                resourcePrefix("${key}_")

                lint {
                    baseline = project.file("lint-baseline.xml")
                }
            }
        }
    }

}