@file:Suppress("SpellCheckingInspection", "ObjectPropertyName")

package com.dede.android_eggs.dls

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.konan.properties.loadProperties
import java.util.Properties

private lateinit var _keyprops: Properties

val Project.keyprops: Properties
    get() {
        if (!::_keyprops.isInitialized) {
            _keyprops = with(rootProject.file("key.properties")) {
                if (exists()) loadProperties(absolutePath) else Properties()
            }
        }
        return _keyprops
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

const val EGG_TASK_GROUP = "egg"
