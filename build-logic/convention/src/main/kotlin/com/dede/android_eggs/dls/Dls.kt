@file:Suppress("SpellCheckingInspection", "ObjectPropertyName")

package com.dede.android_eggs.dls

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import java.util.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties as konanLoadProperties

private lateinit var _keyprops: Properties

val Project.keyprops: Properties
    get() {
        if (!::_keyprops.isInitialized) {
            _keyprops = rootProject.loadProperties("key.properties")
            val stortFilePath = _keyprops.getProperty("storeFile")
            println("Key store file path: $stortFilePath")
        }
        return _keyprops
    }

fun Project.loadProperties(path: String): Properties {
    return with(file(path)) {
        if (exists()) konanLoadProperties(absolutePath) else Properties()
    }
}

fun Properties.getBoolean(key: String, defaultValue: Boolean = false): Boolean {
    return getProperty(key)?.toBoolean() ?: defaultValue
}

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.library(name: String) = findLibrary(name).get()

fun DependencyHandler.marketImplementation(dependencyNotation: Any): Dependency? =
    add("marketImplementation", dependencyNotation)

val Project.javaExtension: JavaPluginExtension
    get() = extensions.getByName<JavaPluginExtension>("java")

fun Project.androidAppComponent(): ApplicationAndroidComponentsExtension? =
    extensions.findByType(ApplicationAndroidComponentsExtension::class.java)

fun <T> Project.android(action: Action<out T>) {
    extensions.configure("android", action)
}

const val EGG_TASK_GROUP = "egg"
