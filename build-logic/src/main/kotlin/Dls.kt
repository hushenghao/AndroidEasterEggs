import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.Installation
import com.android.build.api.dsl.ProductFlavor
import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val Project.catalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = Versions.JAVA_VERSION.toString()
        }
    }
}

fun Project.configureCompose() {
    extensions.configure<
            CommonExtension<
                    BuildFeatures,
                    BuildType,
                    DefaultConfig,
                    ProductFlavor,
                    AndroidResources,
                    Installation>
            >("android") {

        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion =
                catalog.findVersion("compose.kotlin.compiler").get().requiredVersion
        }
    }
}

fun <T : BaseExtension> Project.configureAndroid(configure: Action<T>? = null) {
    extensions.configure<BaseExtension>("android") {
        compileSdkVersion(Versions.COMPILE_SDK)
        buildToolsVersion = Versions.BUILD_TOOLS

        defaultConfig {
            minSdk = Versions.MIN_SDK

            vectorDrawables {
                useSupportLibrary = true
            }
        }

        compileOptions {
            sourceCompatibility = Versions.JAVA_VERSION
            targetCompatibility = Versions.JAVA_VERSION
        }

        configureKotlin()

        @Suppress("UNCHECKED_CAST")
        configure?.execute(this as T)
    }
}