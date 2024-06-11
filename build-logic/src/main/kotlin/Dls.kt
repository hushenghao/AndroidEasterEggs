import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val Project.catalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun NamedDomainObjectContainer<ApplicationBuildType>.createWith(
    name: String,
    with: String? = null,
    configureAction: Action<ApplicationBuildType>
) {
    create(name) {
        if (with != null) {
            initWith(getByName(with))
        }
        configureAction.execute(this)
    }
}

fun <T : BaseExtension> Project.configureAndroid(configure: Action<T>? = null) {

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget = Versions.KTOLIN_JVM_VERSION
        }
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

        compileOptions {
            sourceCompatibility = Versions.JAVA_VERSION
            targetCompatibility = Versions.JAVA_VERSION
        }

        if (configure != null) {
            @Suppress("UNCHECKED_CAST")
            configure.execute(this as T)
        }
    }
}