@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

object Versions {

    const val COMPILE_SDK = 34
    const val BUILD_TOOLS = "34.0.0"
    const val TARGET_SDK = 34
    const val MIN_SDK = 21

    val JAVA_VERSION = JavaVersion.VERSION_17
    val KTOLIN_JVM_VERSION = JvmTarget.JVM_17

    val Project.gitHash: String
        get() {
            return providers.exec {
                commandLine("git", "rev-parse", "--short=7", "HEAD")
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