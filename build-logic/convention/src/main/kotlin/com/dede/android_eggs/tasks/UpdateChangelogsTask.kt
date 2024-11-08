package com.dede.android_eggs.tasks

import android
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register

abstract class UpdateChangelogsTask : Exec() {

    companion object Register : Action<Project> {

        fun register(project: Project) {
            project.afterEvaluate(this)
        }

        override fun execute(project: Project) {
            with(project) {
                val task = tasks.register<UpdateChangelogsTask>("updateChangelogs") {
                    workingDir("${rootDir.absolutePath}/script/changelogs")
                    outputDir = file("${rootDir.absolutePath}/fastlane/metadata/android")
                    observedFiles = files(
                        "${rootDir.absolutePath}/CHANGELOG.md",
                        "${rootDir.absolutePath}/CHANGELOG_zh.md"
                    )
                    group = "custom"
                }

                android.applicationVariants.forEach { variant ->
                    val variantName = variant.name.capitalized()
                    val assembleTask = tasks.findByName("assemble$variantName")
                    assembleTask?.dependsOn(task)
                }
            }
        }

    }

    @get:Input
    abstract val observedFiles: Property<FileCollection>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        @Suppress("LeakingThis")
        commandLine("python3", "changelogs.py")
    }
}
