import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.util.*

object Versions {

    const val COMPILE_SDK = 33
    const val BUILD_TOOLS = "33.0.2"
    const val TARGET_SDK = 33
    const val MIN_SDK = 21

    val Project.gitHash: String
        get() {
            val stdout = ByteArrayOutputStream()
            exec {
                commandLine("git", "rev-parse", "--short", "HEAD")
                standardOutput = stdout
            }
            return stdout.toString().trim()
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