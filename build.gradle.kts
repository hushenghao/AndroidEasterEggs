import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

fun <T : BaseExtension> Project.android(configure: Action<T>? = null) {
    extensions.configure<BaseExtension>("android") {
        defaultConfig {
            vectorDrawables {
                useSupportLibrary = true
            }
        }
        compileOptions {
            sourceCompatibility = Versions.JAVA_VERSION
            targetCompatibility = Versions.JAVA_VERSION
        }
        val kotlinOptions: KotlinJvmOptions? =
            (this as ExtensionAware).extensions.findByType()
        if (kotlinOptions != null) {
            kotlinOptions.jvmTarget = Versions.JAVA_VERSION.toString()
        }
        @Suppress("UNCHECKED_CAST")
        configure?.execute(this as T)
    }
}

allprojects {
    afterEvaluate {
        if (plugins.hasPlugin(AppPlugin::class)) {
            android<AppExtension>()
        } else if (plugins.hasPlugin(LibraryPlugin::class)) {
            val isEgg = path.contains("eggs")
            val hasKotlin = plugins.hasPlugin(libs.plugins.kotlin.android.get().pluginId)
            if (isEgg) {
                plugins.apply(libs.plugins.hilt.android.get().pluginId)
                if (hasKotlin) {
                    plugins.apply("kotlin-kapt")
                }
            }
            android<LibraryExtension> {
                buildFeatures {
                    buildConfig = false
                }
                if (isEgg) {
                    val s = name.substring(0, 1).lowercase()
                    namespace = "com.android_$s.egg"
                    resourcePrefix("${s}_")
                    lint {
                        baseline = project.file("lint-baseline.xml")
                        fatal += listOf("NewApi", "InlineApi")
                    }
                }
            }
            dependencies {
                if (isEgg) {
                    add("implementation", libs.hilt.android)
                    if (hasKotlin) {
                        add("kapt", libs.hilt.compiler)
                    } else {
                        add("annotationProcessor", libs.hilt.compiler)
                    }
                    add("implementation", project(path = ":basic"))
                }
            }
        }
    }
}
