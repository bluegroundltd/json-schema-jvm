package com.theblueground.json.schema.jvm.gradleplugin

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import java.net.URL
import java.net.URLClassLoader

internal fun Project.classLoader(parent: ClassLoader, target: File): ClassLoader {
    val classURLs =
        setOf<URL>(target.toURI().toURL()) + configurations.getByName("runtimeClasspath").map { it.toURI().toURL() }

    logger.debug(buildString {
        appendLine("classpath:")
        appendLine(classURLs.joinToString("\n  * ", prefix = "  * "))
    })

    return URLClassLoader(classURLs.toTypedArray(), parent)
}

internal fun Project.getTargetOfMergeOutputClasspath(): File = layout.buildDirectory.dir("classes-merged").get().asFile

internal fun Project.getSourceSets(): Set<SourceSet> {
    val sourceSetContainer = this.properties["sourceSets"] as SourceSetContainer?

    return sourceSetContainer
        ?.filter { !it.name.contains("test", true) }
        ?.toSet()
        .orEmpty()
}

internal fun Project.getSrcAbsolutePath(): File {
    return getSourceSets().firstOrNull()?.java?.srcDirTrees?.firstOrNull()?.dir?.parentFile ?: file("/tmp")
}

internal fun Project.mergeOutputClasspath() {
    val target = getTargetOfMergeOutputClasspath()
    val sources = mutableSetOf<File>()
    val sourceSets = getSourceSets()

    target.deleteRecursively()

    for (sourceSet in sourceSets) {
        sources.addAll(sourceSet.output.classesDirs)
    }

    copy {
        from(sources)
        into(target)
    }
}

internal fun Project.getClassConicalNamesFormMergeOutput(): Set<String> {
    val mergedClassRoot = getTargetOfMergeOutputClasspath()
    val tree = fileTree(
        mapOf(
            "dir" to mergedClassRoot.path,
            "include" to "**/*.class"
        )
    )

    return tree
        .files
        .filter { !it.name.contains("$") }
        .map {
            it.path
                .replace("${mergedClassRoot.path}/", "")
                .replace("/", ".")
                .replace(".class", "")
        }.toSet()
}

internal fun Project.getScanPathClassConicalNames(jsonSchemaJvmExtension: JsonSchemaJvmExtension): Set<String> {
    val allClasses = getClassConicalNamesFormMergeOutput()
    val scanPackages = jsonSchemaJvmExtension.packageToScan.get()
    return allClasses
        .filter { allClass ->
            scanPackages.any { scanPackage ->
                allClass.startsWith(scanPackage, true)
            }
        }
        .toSet()
}