import org.gradle.api.Project
import java.util.Properties

object Versions {

    const val COMPILE_SDK = 34
    const val BUILD_TOOLS = "34.0.0"
    const val TARGET_SDK = 33
    const val MIN_SDK = 21

    val Project.gitHash: String
        get() {
            return providers.exec {
                commandLine("git", "rev-parse", "--short", "HEAD")
            }.standardOutput.asText.get().trim()
        }

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
}