package com.theblueground.json.schema.jvm.gradleplugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

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
}