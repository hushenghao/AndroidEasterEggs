package com.dede.android_eggs.tasks

import com.android.build.api.variant.Variant
import com.dede.android_eggs.dls.EGG_TASK_GROUP
import com.dede.android_eggs.dls.androidAppComponent
import com.dede.android_eggs.dls.getBoolean
import com.dede.android_eggs.dls.loadProperties
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register

abstract class UpdateChangelogsTask : Exec() {

    companion object Register {

        private const val TASK_NAME = "updateChangelogs"

        private const val PROPERTY_KEY = "eggs.updateChangelogs"

        fun register(project: Project) {
            with(project) {
                val roodPath = rootDir.absolutePath
                val task = tasks.register<UpdateChangelogsTask>(TASK_NAME) {
                    workingDir("$roodPath/script/changelogs")

                    outputDir = file("$roodPath/fastlane/metadata/android")
                    changelogsFile = file("$roodPath/CHANGELOG.md")
                    changelogsZhFile = file("$roodPath/CHANGELOG_zh.md")
                    group = EGG_TASK_GROUP
                }

                val enableDepends = rootProject.loadProperties("local.properties")
                    .getBoolean(PROPERTY_KEY)

                if (enableDepends) {
                    val variantNameSet = HashSet<String>()
                    androidAppComponent()?.onVariants { variant: Variant ->
                        val variantName = variant.name.capitalized()
                        variantNameSet.add(variantName)
                    }
                    afterEvaluate {
                        variantNameSet.forEach { variantName ->
                            val assembleTask = tasks.findByName("assemble$variantName")
                            assembleTask?.dependsOn(task)
                        }
                    }
                }
            }
        }

    }

    @get:InputFile
    abstract val changelogsFile: RegularFileProperty

    @get:InputFile
    abstract val changelogsZhFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        @Suppress("LeakingThis")
        commandLine("python3", "changelogs.py")
    }
}
