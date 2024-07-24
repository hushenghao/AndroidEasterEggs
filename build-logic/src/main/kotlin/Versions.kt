import org.gradle.jvm.toolchain.JavaLanguageVersion

object Versions {

    const val COMPILE_SDK = 35
    const val BUILD_TOOLS = "35.0.0"
    const val TARGET_SDK = 35
    const val MIN_SDK = 21

    val JAVA_VERSION = JavaLanguageVersion.of(17)

}
