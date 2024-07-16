pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "json-schema-jvm"
include("gradle-plugin")
include("generate-schema-annotation")
include("test-json-schema-jvm")
