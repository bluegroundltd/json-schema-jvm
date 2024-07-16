package com.theblueground.json.schema.jvm.gradleplugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named

class JsonSchemaJvmPlugin: Plugin<Project> {

    companion object {
        private const val PLUGIN_CONFIGURATION_NAME = "jsonSchemaJvmGeneration"
    }

    override fun apply(target: Project) {

        val jsonSchemaJvmExt = target
            .extensions
            .create<JsonSchemaJvmExtension>(JsonSchemaJvmExtension.NAME)

        target.tasks
            .register(JsonSchemaJvmGenerationTask.NAME, JsonSchemaJvmGenerationTask::class.java, jsonSchemaJvmExt)

        target.tasks
            .named<JsonSchemaJvmGenerationTask>(JsonSchemaJvmGenerationTask.NAME)
            .configure {
                dependsOn(target.tasks.getByPath("classes"))
            }

        target.configurations.create(PLUGIN_CONFIGURATION_NAME)
    }
}
