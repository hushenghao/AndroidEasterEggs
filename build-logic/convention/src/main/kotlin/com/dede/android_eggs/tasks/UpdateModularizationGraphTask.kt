package com.dede.android_eggs.tasks

import com.dede.android_eggs.dls.EGG_TASK_GROUP
import com.dede.android_eggs.tasks.ModularizationGraphMermaid.Dependency
import com.dede.android_eggs.tasks.ModularizationGraphMermaid.Module
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

abstract class UpdateModularizationGraphTask : DefaultTask() {

    @get:Input
    abstract val modulePaths: ListProperty<String>

    @get:Input
    abstract val projectDependencyEdges: ListProperty<String>

    @get:OutputFile
    abstract val documentFile: RegularFileProperty

    @TaskAction
    fun update() {
        val modules = modulePaths.get()
            .map { Module(path = it) }
        val moduleByPath = modules.associateBy { it.path }
        val moduleOrder = modules.mapIndexed { index, module -> module.path to index }.toMap()
        val dependencies = projectDependencyEdges.get()
            .mapNotNull { it.toDependency() }
            .filter { it.source in moduleByPath && it.target in moduleByPath }
            .distinct()
            .sortedWith(
                compareBy(
                    { moduleOrder.getValue(it.source) },
                    { moduleOrder.getValue(it.target) }
                )
            )

        val graph = ModularizationGraphMermaid.build(modules, dependencies)
        val document = documentFile.asFile.get()
        document.writeText(replaceModularizationGraph(document.readText(), graph))
    }

    private fun replaceModularizationGraph(document: String, graph: String): String {
        val graphBlock = "$GRAPH_START\n```mermaid\n$graph\n```\n$GRAPH_END"
        val graphStart = document.indexOf(GRAPH_START)
        require(graphStart >= 0) { "Missing $GRAPH_START in BUILD.md" }

        val graphEnd = document.indexOf(GRAPH_END, startIndex = graphStart + GRAPH_START.length)
        require(graphEnd >= 0) { "Missing $GRAPH_END in BUILD.md" }

        return document.replaceRange(graphStart, graphEnd + GRAPH_END.length, graphBlock)
    }

    private fun String.toDependency(): Dependency? {
        val separatorIndex = indexOf(DEPENDENCY_EDGE_SEPARATOR)
        if (separatorIndex < 0) return null
        val source = substring(0, separatorIndex)
        val target = substring(separatorIndex + DEPENDENCY_EDGE_SEPARATOR.length)
        return Dependency(source, target)
    }

    companion object Register {

        private const val TASK_NAME = "updateModularizationGraph"
        private const val GRAPH_START = "<!-- modularization-graph:start -->"
        private const val GRAPH_END = "<!-- modularization-graph:end -->"
        private const val DEPENDENCY_EDGE_SEPARATOR = "|"

        fun register(project: Project) {
            with(project) {
                if (tasks.names.contains(TASK_NAME)) return

                val task = tasks.register<UpdateModularizationGraphTask>(TASK_NAME) {
                    group = EGG_TASK_GROUP
                    description = "Updates the Mermaid modularization graph in BUILD.md."

                    documentFile.set(layout.projectDirectory.file("BUILD.md"))
                }
                gradle.projectsEvaluated {
                    task.configure {
                        modulePaths.set(collectModulePaths(project.rootProject))
                        projectDependencyEdges.set(collectProjectDependencyEdges(project.rootProject))
                    }
                }
            }
        }

        private fun collectModulePaths(rootProject: Project): List<String> {
            return rootProject.subprojects
                .filter { it.buildFile.isFile }
                .map { it.path }
        }

        private fun collectProjectDependencyEdges(rootProject: Project): List<String> {
            return rootProject.subprojects
                .filter { it.buildFile.isFile }
                .flatMap { sourceProject ->
                    sourceProject.configurations
                        .filter { configuration -> configuration.isCanBeDeclared }
                        .flatMap { configuration ->
                            configuration.dependencies
                                .withType(ProjectDependency::class.java)
                                .mapNotNull { dependency ->
                                    if (dependency.path == sourceProject.path) {
                                        return@mapNotNull null
                                    }
                                    sourceProject.path +
                                        DEPENDENCY_EDGE_SEPARATOR +
                                        dependency.path
                                }
                        }
                }
                .distinct()
        }
    }
}
