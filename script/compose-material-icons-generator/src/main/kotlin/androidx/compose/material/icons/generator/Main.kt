package androidx.compose.material.icons.generator

import java.io.File

fun main() {
    val dir = File("script/compose-material-icons-generator")
    val iconDirectories = File(dir, "raw-icons")
        .listFiles()!!
        .filter { it.isDirectory }
    val icons = IconProcessor(
        iconDirectories = iconDirectories,
        expectedApiFile = File(dir, "api/icons.txt"),
        generatedApiFile = File(dir, "api/icons.txt"),
        expectedAutoMirroredApiFile = File(dir, "api/automirrored_icons.txt"),
        generatedAutoMirroredApiFile = File(dir, "api/automirrored_icons.txt")
    ).process()

//    val outputDir = File(dir, "build/outputs")
    val outputDir = File("app/src/main/java")
    IconWriter(icons)
        .generateTo(outputDir, iconNamePredicate = { true })
}