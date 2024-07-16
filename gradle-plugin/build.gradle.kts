@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(gradleApi())
    implementation("io.github.bluegroundltd:generate-schema-annotation:1.0.0")
    implementation(platform("com.github.victools:jsonschema-generator-bom:4.35.0"))
    implementation("com.github.victools:jsonschema-generator")
    implementation("com.github.victools:jsonschema-module-jackson")
    implementation("com.kjetland:mbknor-jackson-jsonschema_2.13:1.0.39")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}

gradlePlugin {
    website = "https://github.com/bluegroundltd/json-schema-jvm"
    vcsUrl = "https://github.com/bluegroundltd/json-schema-jvm.git"
    plugins {
        create("generateJsonSchemaJvm") {
            id = "com.theblueground.json.schema.jvm.gradle-plugin"
            version = "1.0.0"
            displayName = "JSON Schema Generation Plugin"
            description = "Generate JSON schema from annotated classes"
            tags = listOf("json", "schema")
            implementationClass = "com.theblueground.json.schema.jvm.gradleplugin.JsonSchemaJvmPlugin"
        }
    }
}