import com.android.build.api.dsl.CompileSdkSpec
import com.android.build.api.dsl.TargetSdkSpec
import org.gradle.jvm.toolchain.JavaLanguageVersion

object Versions {

    val compileSdk: CompileSdkSpec.() -> Unit = {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    const val BUILD_TOOLS = "36.1.0"

    val targetSdk: TargetSdkSpec.() -> Unit = {
        version = release(36)
    }

    const val MIN_SDK = 23

    val JAVA_VERSION: JavaLanguageVersion = JavaLanguageVersion.of(17)

}
