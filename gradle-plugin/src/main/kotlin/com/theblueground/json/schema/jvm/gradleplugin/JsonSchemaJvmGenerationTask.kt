package com.theblueground.json.schema.jvm.gradleplugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaDraft
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import io.github.bluegroundltd.GenerateJsonSchema
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject
import kotlin.reflect.full.findAnnotation


internal abstract class JsonSchemaJvmGenerationTask @Inject constructor(
    private val jsonSchemaJvmExtension: JsonSchemaJvmExtension
) : DefaultTask() {

    private val logger: Logger = Logging.getLogger(
        "JsonSchemaJVM:${JsonSchemaJvmGenerationTask::class.java.simpleName}"
    )

    companion object {
        const val NAME = "jsonSchemaJvmGenerationTask"
        private val MAPPER = ObjectMapper()
    }

    @TaskAction
    fun run() {
        project.mergeOutputClasspath()

        val classesDirs = project.getTargetOfMergeOutputClasspath()
        val canonicalClassNames = project.getScanPathClassConicalNames(jsonSchemaJvmExtension)
        val classLoader = project.classLoader(javaClass.classLoader, classesDirs)

        logger.info("Class names with annotation: ${canonicalClassNames.size}")

        val thread = Thread.currentThread()
        val originalClassLoader = thread.contextClassLoader

        try {
            thread.contextClassLoader = classLoader

            val srcPath = project.getSrcAbsolutePath().path
            val outputPath = jsonSchemaJvmExtension.outputDirectory.getOrElse("$srcPath/json-schema-jvm")

            val projectClasses = canonicalClassNames
                .map { classLoader.loadClass(it).kotlin }
                .toSet()

            val generateJsonSchemaAnnotations = projectClasses
                .filter { it.findAnnotation<GenerateJsonSchema>() != null }
                .toSet()

            logger.info("Annotated classes ${generateJsonSchemaAnnotations.size}")

            val jsonSchemaConfig =
                JsonSchemaConfig.vanillaJsonSchemaDraft4().withJsonSchemaDraft(JsonSchemaDraft.DRAFT_07)
            val schemaGen = JsonSchemaGenerator(MAPPER, jsonSchemaConfig)

            generateJsonSchemaAnnotations.forEach {
                logger.info("Generate JSON schema file")
                logger.info("Generating schema for: ${it.qualifiedName}")

                val loadedClazz = classLoader.loadClass(it.qualifiedName).kotlin

                loadedClazz.constructors.forEach {
                    logger.info("Constructor: ${it.name}")

                    it.parameters.forEach { parameter ->
                        logger.info("Parameter: ${parameter.name}")
                    }
                }

                val schema = schemaGen.generateJsonSchema(classLoader.loadClass(it.qualifiedName))
                val schemaJsonText = MAPPER.writeValueAsString(schema)

                logger.info("Generated JSON schema: $schemaJsonText")

                val generatedJsonSchemaOutputFile = File("${outputPath}/${it.simpleName?.lowercase()}.json")
                val generatedJsonSchemaOutputDir = generatedJsonSchemaOutputFile.parentFile

                if (!generatedJsonSchemaOutputDir.exists()) {
                    generatedJsonSchemaOutputDir.mkdirs()
                }

                generatedJsonSchemaOutputFile.writeText(schemaJsonText)
            }

        } finally {
            thread.contextClassLoader = originalClassLoader
        }
    }

    override fun onlyIf(onlyIfReason: String, onlyIfSpec: Spec<in Task>) {
        TODO("Not yet implemented")
    }

    override fun doNotTrackState(reasonNotToTrackState: String) {
        TODO("Not yet implemented")
    }

    override fun notCompatibleWithConfigurationCache(reason: String) {
        TODO("Not yet implemented")
    }

    override fun setOnlyIf(onlyIfReason: String, onlyIfSpec: Spec<in Task>) {
        TODO("Not yet implemented")
    }
}