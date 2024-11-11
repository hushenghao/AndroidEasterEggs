@file:Suppress("SpellCheckingInspection", "ObjectPropertyName")

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.util.Properties

private lateinit var _keyprops: Properties

val Project.keyprops: Properties
    get() {
        if (!::_keyprops.isInitialized) {
            _keyprops = Properties().apply {
                (rootProject.file("key.properties").takeIf { it.exists() } ?: return@apply)
                    .inputStream().use(::load)
            }
        }
        return _keyprops
    }

val Project.catalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun DependencyHandler.implementation(flavor: String, dependencyNotation: Any): Dependency? =
    add("${flavor}Implementation", dependencyNotation)

val Project.javaExtension: JavaPluginExtension
    get() = extensions.getByName<JavaPluginExtension>("java")

val Project.android: AppExtension
    get() = extensions.getByName<AppExtension>("android")

fun Project.androidAppComponent(): ApplicationAndroidComponentsExtension? =
    extensions.findByType(ApplicationAndroidComponentsExtension::class.java)

const val EGG_TASK_GROUP = "egg"

fun <T : BaseExtension> Project.configureAndroid(configure: Action<T>? = null) {

    javaExtension.toolchain {
        languageVersion.set(Versions.JAVA_VERSION)
    }

    kotlinExtension.jvmToolchain {
        languageVersion.set(Versions.JAVA_VERSION)
    }

    extensions.configure<BaseExtension>("android") {
        compileSdkVersion(Versions.COMPILE_SDK)
        buildToolsVersion = Versions.BUILD_TOOLS

        defaultConfig {
            minSdk = Versions.MIN_SDK

            vectorDrawables {
                useSupportLibrary = true
            }
        }

        if (configure != null) {
            @Suppress("UNCHECKED_CAST")
            configure.execute(this as T)
        }
    }
}