package com.dede.android_eggs.tasks

internal object ModularizationGraphMermaid {

    fun build(
        modules: List<Module>,
        dependencies: List<Dependency>,
    ): String {
        val modulesByGroup = modules.groupBy { it.group }
        val moduleByPath = modules.associateBy { it.path }
        val builder = StringBuilder()

        builder.appendLine("flowchart LR")
        appendNodes(builder, modulesByGroup["app"].orEmpty(), indent = "    ")
        appendNodes(builder, modulesByGroup["base"].orEmpty(), indent = "    ")
        builder.appendLine()

        appendSubgraph(builder, "core", modulesByGroup["core"].orEmpty())
        appendSubgraph(builder, "feature", modulesByGroup["feature"].orEmpty())
        appendSubgraph(builder, "eggs", modulesByGroup["eggs"].orEmpty())
        appendSubgraph(builder, "script", modulesByGroup["script"].orEmpty())

        builder.appendLine("    classDef appModule fill:#ffebee,stroke:#ff1744,color:#ff1744,rx:8,ry:8")
        builder.appendLine("    classDef baseModule fill:#f9fafb,stroke:#6b7280,color:#6b7280,rx:8,ry:8")
        builder.appendLine("    classDef coreModule fill:#eff6ff,stroke:#2f80ed,color:#2f80ed,rx:8,ry:8")
        builder.appendLine("    classDef featureModule fill:#f0fdf4,stroke:#27ae60,color:#27ae60,rx:8,ry:8")
        builder.appendLine("    classDef eggsModule fill:#fff7ed,stroke:#f2994a,color:#f2994a,rx:8,ry:8")
        builder.appendLine("    classDef scriptModule fill:#faf5ff,stroke:#9b51e0,color:#9b51e0,rx:8,ry:8")
        builder.appendLine()

        appendClass(builder, modulesByGroup["app"].orEmpty(), "appModule")
        appendClass(builder, modulesByGroup["base"].orEmpty(), "baseModule")
        appendClass(builder, modulesByGroup["core"].orEmpty(), "coreModule")
        appendClass(builder, modulesByGroup["feature"].orEmpty(), "featureModule")
        appendClass(builder, modulesByGroup["eggs"].orEmpty(), "eggsModule")
        appendClass(builder, modulesByGroup["script"].orEmpty(), "scriptModule")
        builder.appendLine()

        builder.appendLine("    style core fill:#f8fbff,stroke:#2f80ed,color:#2f80ed,rx:8,ry:8")
        builder.appendLine("    style feature fill:#f8fffa,stroke:#27ae60,color:#27ae60,rx:8,ry:8")
        builder.appendLine("    style eggs fill:#fffaf4,stroke:#f2994a,color:#f2994a,rx:8,ry:8")
        builder.appendLine("    style script fill:#fdfaff,stroke:#9b51e0,color:#9b51e0,rx:8,ry:8")
        builder.appendLine()

        dependencies.forEach { dependency ->
            val source = moduleByPath.getValue(dependency.source)
            val target = moduleByPath.getValue(dependency.target)
            builder.appendLine("    ${source.id} --> ${target.id}")
        }
        builder.appendLine()

        appendLinkStyles(builder, dependencies, moduleByPath)

        return builder.toString().trimEnd()
    }

    data class Module(
        val path: String,
        val id: String = path.toMermaidId(),
        val group: String = path.groupName(),
    )

    data class Dependency(
        val source: String,
        val target: String,
    )

    private fun appendNodes(
        builder: StringBuilder,
        modules: List<Module>,
        indent: String,
    ) {
        modules.forEach { module ->
            builder.appendLine("$indent${module.id}(\"${module.path}\")")
        }
    }

    private fun appendSubgraph(builder: StringBuilder, group: String, modules: List<Module>) {
        if (modules.isEmpty()) return

        builder.appendLine("    subgraph $group[\":$group\"]")
        appendNodes(builder, modules, indent = "        ")
        builder.appendLine("    end")
        builder.appendLine()
    }

    private fun appendClass(builder: StringBuilder, modules: List<Module>, className: String) {
        if (modules.isEmpty()) return

        builder.appendLine("    class ${modules.joinToString(",") { it.id }} $className")
    }

    private fun appendLinkStyles(
        builder: StringBuilder,
        dependencies: List<Dependency>,
        moduleByPath: Map<String, Module>,
    ) {
        dependencies.indices
            .groupBy { index ->
                val dependency = dependencies[index]
                val source = moduleByPath.getValue(dependency.source)
                val target = moduleByPath.getValue(dependency.target)
                if (source.group == "app") target.group else source.group
            }
            .forEach { (group, indices) ->
                val color = group.linkColor() ?: return@forEach
                builder.appendLine(
                    "    linkStyle ${indices.joinToString(",")} stroke:$color,stroke-width:2px"
                )
            }
    }

    private fun String.toMermaidId(): String {
        return removePrefix(":")
            .replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
            .replace(Regex("[^A-Za-z0-9]+"), "_")
            .trim('_')
            .lowercase()
            .ifEmpty { "root" }
    }

    private fun String.groupName(): String {
        return when {
            this == ":app" -> "app"
            this == ":basic" || this == ":jvm-basic" -> "base"
            startsWith(":core:") -> "core"
            startsWith(":feature:") -> "feature"
            startsWith(":eggs:") -> "eggs"
            startsWith(":script:") -> "script"
            else -> "base"
        }
    }

    private fun String.linkColor(): String? {
        return when (this) {
            "core" -> "#2f80ed"
            "feature" -> "#27ae60"
            "eggs" -> "#f2994a"
            "script" -> "#9b51e0"
            "app" -> "#ff1744"
            "base" -> "#6b7280"
            else -> null
        }
    }
}
