import com.android.build.api.dsl.CompileSdkSpec
import com.android.build.api.dsl.TargetSdkSpec
import org.gradle.jvm.toolchain.JavaLanguageVersion

object Versions {

    val compileSdk: CompileSdkSpec.() -> Unit = {
//        version = release(36) {
//            minorApiLevel = 1
//        }
        version = preview("CinnamonBun")
    }

    const val BUILD_TOOLS = "37.0.0-rc2"

    val targetSdk: TargetSdkSpec.() -> Unit = {
        version = release(36)
    }

    const val MIN_SDK = 23

    val JAVA_VERSION: JavaLanguageVersion = JavaLanguageVersion.of(17)

}
