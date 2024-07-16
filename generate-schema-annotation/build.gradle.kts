import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
    id("com.vanniktech.maven.publish") version "0.29.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}

mavenPublishing {
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.None(),
            sourcesJar = true,
        )
    )

    coordinates("io.github.bluegroundltd", "generate-schema-annotation", "1.0.0")

    pom {
        name.set("GenerateSchemaAnnotation")
        description.set("Marker annotation for JVM, in order to generate JSON schemas from classes")
        inceptionYear.set("2024")
        url.set("https://github.com/bluegroundltd/json-schema-jvm/generate-schema-annotation")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/bluegroundltd/json-schema-jvm/blob/main/LICENSE")
                distribution.set("https://github.com/bluegroundltd/json-schema-jvm/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("bluegroundltd")
                url.set("https://github.com/bluegroundltd/")
            }
        }
        scm {
            url.set("https://github.com/bluegroundltd/json-schema-jvm/")
            connection.set("scm:git:git://github.com/bluegroundltd/json-schema-jvm.git")
            developerConnection.set("scm:git:ssh://git@github.com/bluegroundltd/json-schema-jvm.git")
        }
    }
}