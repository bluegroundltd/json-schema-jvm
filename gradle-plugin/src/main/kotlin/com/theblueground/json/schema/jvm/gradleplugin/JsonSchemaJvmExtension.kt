package com.theblueground.json.schema.jvm.gradleplugin

import java.io.File
import javax.inject.Inject
import kotlin.reflect.KClass
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.property

open class JsonSchemaJvmExtension @Inject constructor(
    objects: ObjectFactory
) {

    companion object {
        const val NAME = "jsonSchemaJvmExtension"
    }

    @get:Input
    val packageToScan: ListProperty<String> = objects.listProperty(String::class.java)

    @get:Input
    val outputDirectory: Property<String> = objects.property(String::class.java)

    fun getOutputPath(project: Project): File {
        val srcPath = project.getSrcAbsolutePath().path
        return File(this.outputDirectory.getOrElse("$srcPath/json-schema-jvm"))
    }

    @get:Input
    val writeDatesAsTimestamps: Property<Boolean> = objects.property(Boolean::class.java)

    @get:Input
    val nameFormatter: Property<(KClass<*>) -> String> = objects.property()

    fun getNameFormatter(): (KClass<*>) -> String = nameFormatter.getOrElse { klass ->
        "${klass.simpleName?.lowercase()}.json"
    }
}