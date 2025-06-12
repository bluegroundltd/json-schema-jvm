package com.theblueground.json.schema.jvm.gradleplugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaDraft
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import io.github.bluegroundltd.GenerateJsonSchema
import java.io.File
import javax.inject.Inject
import kotlin.reflect.full.findAnnotation
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

internal abstract class JsonSchemaJvmGenerationTask @Inject constructor(
    private val jsonSchemaJvmExtension: JsonSchemaJvmExtension,
) : DefaultTask() {

    companion object {
        const val NAME = "jsonSchemaJvmGenerationTask"
    }

    private val logger: Logger = Logging.getLogger(
        "JsonSchemaJVM:${JsonSchemaJvmGenerationTask::class.java.simpleName}"
    )

    private val jsonSchemaConfig =
        JsonSchemaConfig.vanillaJsonSchemaDraft4().withJsonSchemaDraft(JsonSchemaDraft.DRAFT_07)

    private val nameFormatter = jsonSchemaJvmExtension.getNameFormatter()

    private val mapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        registerModule(KotlinModule.Builder().build())
        configure(
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
            jsonSchemaJvmExtension.writeDatesAsTimestamps.getOrElse(true)
        )
    }

    private val outputPath = jsonSchemaJvmExtension.getOutputPath(project)

    private val prettyMapper: ObjectWriter = mapper.writerWithDefaultPrettyPrinter()

    private val schemaGen: JsonSchemaGenerator = JsonSchemaGenerator(mapper, jsonSchemaConfig)

    @TaskAction
    fun run() {
        if (!outputPath.exists()) {
            outputPath.mkdirs()
        }

        project.mergeOutputClasspath()
        val classesDirs = project.getTargetOfMergeOutputClasspath()
        val canonicalClassNames = project.getScanPathClassConicalNames(jsonSchemaJvmExtension)
        val classLoader = project.classLoader(javaClass.classLoader, classesDirs)

        logger.info("Class names with annotation: ${canonicalClassNames.size}")

        val thread = Thread.currentThread()
        val originalClassLoader = thread.contextClassLoader

        try {
            thread.contextClassLoader = classLoader

            val projectClasses = canonicalClassNames
                .map { classLoader.loadClass(it).kotlin }
                .toSet()

            val generateJsonSchemaAnnotations = projectClasses
                .filter { it.findAnnotation<GenerateJsonSchema>() != null }
                .toSet()

            logger.info("Annotated classes ${generateJsonSchemaAnnotations.size}")

            generateJsonSchemaAnnotations.forEach { klass ->
                logger.info("Generate JSON schema file")
                logger.info("Generating schema for: ${klass.qualifiedName}")

                val loadedClazz = classLoader.loadClass(klass.qualifiedName).kotlin

                loadedClazz.constructors.forEach {
                    logger.info("Constructor: ${it.name}")

                    it.parameters.forEach { parameter ->
                        logger.info("Parameter: ${parameter.name}")
                    }
                }

                val schema = schemaGen.generateJsonSchema(classLoader.loadClass(klass.qualifiedName))
                val schemaJsonText = prettyMapper.writeValueAsString(schema)

                logger.info("Generated JSON schema: $schemaJsonText")

                val generatedJsonSchemaOutputFile = File(outputPath, nameFormatter.invoke(klass))
                generatedJsonSchemaOutputFile.writeText(schemaJsonText)
            }

        } finally {
            thread.contextClassLoader = originalClassLoader
        }
    }

}
