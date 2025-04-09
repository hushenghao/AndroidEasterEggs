import org.gradle.jvm.toolchain.JavaLanguageVersion

object Versions {

    const val COMPILE_SDK = 36
    const val BUILD_TOOLS = "36.0.0"
    const val TARGET_SDK = 36
    const val MIN_SDK = 21

    val JAVA_VERSION: JavaLanguageVersion = JavaLanguageVersion.of(17)

}
